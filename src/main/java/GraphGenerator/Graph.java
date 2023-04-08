package GraphGenerator;

import Environment.Wall;
import Utils.Vector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
    // Found empirically, TODO: AUTOMATIZE STEP SIZE SELECTION
    private final static double STEP_SIZE = 1.33;
    private final static Vector[] POSSIBLE_NEIGHBORS_POSITION_DIFFERENCE = {
            new Vector(0., STEP_SIZE),
            new Vector(STEP_SIZE, 0.),
            new Vector(0., -STEP_SIZE),
            new Vector(-STEP_SIZE, 0.),

    };
    public static String dxfDataFileName = "/DXF_DATA.txt";
    public static String graphBackupFileName = "/graph.csv";
    private final String dxfName;
    private final Map<Integer, Node> nodes;
    private final List<Wall> walls;

    public Graph(List<Wall> walls, List<Wall> exits, List<Vector> accessiblePositions, Vector initialPosition, String dxfName) {
        this.nodes = new HashMap<>();
        this.walls = walls;
        this.dxfName = dxfName;
        System.out.println("\tGenerating graph.");
        this.generateGraph(initialPosition, exits);
        System.out.println("\tGraph generated.");
        System.out.println("\tReducing graph.");
        this.reduceGraph(walls, accessiblePositions);
        System.out.println("\tGraph reduced.");
    }

    public Graph(Map<Integer, Node> nodes, List<Wall> walls, String dxfName) {
        this.nodes = nodes;
        this.walls = walls;
        this.dxfName = dxfName;
        System.out.println("\tGraph backup imported.");
    }

    public boolean isPositionAccessible(Vector origin, Vector destiny, double radius) {
        // checks if whole circumference of the circle can access to destiny
        // for this, picks "highest" point in circumferences and checks if it can see the "highest" point in destination
        // with "highest", we refer to the perpendicular point to the distance vector in the circumference
        Vector distance = destiny.subtract(origin);
        double originalTetha = Math.atan2(distance.getY(), distance.getX());

        return isProjectedPositionVisible(origin, destiny, radius, originalTetha + Math.PI / 2)
                && isProjectedPositionVisible(origin, destiny, radius, originalTetha - Math.PI / 2);
    }

    private boolean isProjectedPositionVisible(Vector origin, Vector destiny, double radius, double theta) {
        Vector projectedPoint = new Vector(Math.cos(theta), Math.sin(theta)).scalarMultiply(radius);
        Vector originProjectedPoint = origin.add(projectedPoint);
        Vector destinyProjectedPoint = destiny.add(projectedPoint);

        // only if origin projected position is reachable from origin
        // AND projected position on destination is reachable from projected position on origin
        //      returns TRUE
        return isPositionVisibleWithinWalls(origin, originProjectedPoint, this.walls) &&
                isPositionVisibleWithinWalls(originProjectedPoint, destinyProjectedPoint, this.walls);
    }

    private boolean isPositionVisibleWithinWalls(Vector origin, Vector destiny, List<Wall> walls) {
        for (Wall wall : walls)
            if (wall.intersectsLine(origin, destiny) && !wall.contains(destiny))
                // wall intersects the path from origin and destiny, and destiny is not in the wall
                return false;

        return true;
    }

    private Node getClosestNode(Vector position, Predicate<Vector> predicate) {
        Node bestNode = null;
        double minDistance = Double.MAX_VALUE;
        double distanceToNode;
        for (Node node : this.nodes.values()) {
            if (predicate.test(node.getPosition())) {
                distanceToNode = node.getPosition().distance(position);
                if (distanceToNode < minDistance) {
                    bestNode = node;
                    minDistance = distanceToNode;
                }
            }
        }
        return bestNode;
    }

    private Node getClosestVisibleNode(Vector from) {
        Node bestNode = this.getClosestNode(from,
                nodePosition -> isPositionVisibleWithinWalls(from, nodePosition, walls));
        if (bestNode == null) {
            throw new RuntimeException("Zero nodes reachable from position: " + from);
        }
        return bestNode;
    }

    public Node getClosestAccessibleNode(Vector from, double radius) {
        Node bestNode = this.getClosestNode(from,
                nodePosition -> isPositionAccessible(from, nodePosition, radius));
        if (bestNode == null) {
            // try without radius consideration
            return getClosestVisibleNode(from);
        } else return bestNode;
    }

    public NodePath getPathToPosition(Vector fromPosition, Vector toPosition, double radius) {
        if (this.isPositionAccessible(fromPosition, toPosition, radius))
            return new NodePath();

        Node fromNode = this.getClosestAccessibleNode(fromPosition, radius);

        NodePath fullPath = this.AStar(fromNode, toPosition, radius);
        if (fullPath == null) {
            // none path found
            return null;
        }

        return pathReducer(fromPosition, toPosition, fullPath, radius);
    }

    // initialPosition has to be a valid position, from this node the graph will expand
    // considers exits in order to avoid the nodes from going out of the environment indefinitely, this way they stop before going through the exit
    private void generateGraph(Vector initialPosition, List<Wall> extraWalls) {
        List<Wall> wallsToConsider = new ArrayList<>(this.walls);
        wallsToConsider.addAll(extraWalls);

        Node root = new Node(initialPosition);
        this.nodes.put(root.getId(), root);

        List<Node> notVisitedNodes = new ArrayList<>();
        notVisitedNodes.add(root);
        HashMap<Vector, Node> nodesByPosition = new HashMap<>();
        nodesByPosition.put(root.getPosition(), root);

        while (!notVisitedNodes.isEmpty()) {
            Node current = notVisitedNodes.remove(0);
            List<Node> currentNeighbours = new ArrayList<>();
            Vector startPosition = current.getPosition();

            for (Vector difference : POSSIBLE_NEIGHBORS_POSITION_DIFFERENCE) {
                Vector possibleNeighbourPosition = difference.add(startPosition);

                Node neighbor;
                if (nodesByPosition.containsKey(possibleNeighbourPosition)) {
                    // position already considered, is not necessary to create the node
                    neighbor = nodesByPosition.get(possibleNeighbourPosition);

                    // check if its visible
                    if (isPositionVisibleWithinWalls(startPosition, possibleNeighbourPosition, wallsToConsider)) {
                        currentNeighbours.add(neighbor);
                        neighbor.addNeighbor(current.getId());
                    }

                } else if (isPositionVisibleWithinWalls(startPosition, possibleNeighbourPosition, wallsToConsider)) {
                    // position not considered and valid
                    neighbor = new Node(possibleNeighbourPosition);
                    neighbor.addNeighbor(current.getId());
                    this.nodes.put(neighbor.getId(), neighbor);
                    nodesByPosition.put(possibleNeighbourPosition, neighbor);

                    currentNeighbours.add(neighbor);
                    notVisitedNodes.add(neighbor);
                }
            }

            current.addNeighbors(currentNeighbours);
        }

        // add nodes on exits for agents that summon outside the establishment
        // and add an extra mirrored node outside just in case
        for (Wall wall : extraWalls) {
            Node closestNode = this.getClosestVisibleNode(wall.getCentroid());
            Node mirrorNode = new Node(getMirroredPosition(wall, closestNode.getPosition()));
            this.nodes.put(mirrorNode.getId(), mirrorNode);
            if (!isPositionVisibleWithinWalls(closestNode.getPosition(), mirrorNode.getPosition(), this.walls)) {
                // it is necessary to add an intermediary node in the door
                Node node = new Node(wall.getCentroid());
                node.addNeighbor(closestNode.getId());
                this.nodes.put(node.getId(), node);
                closestNode.addNeighbor(node.getId());
                mirrorNode.addNeighbor(node.getId());
                node.addNeighbor(closestNode.getId());
                node.addNeighbor(mirrorNode.getId());
            } else {
                closestNode.addNeighbor(mirrorNode.getId());
                mirrorNode.addNeighbor(closestNode.getId());
            }
        }
    }

    private void merge(Node node1, Node node2, Vector newPosition,
                       Map<Integer, List<Vector>> positionsAccessibleByNodeId, List<Vector> positionsAccessibleByBothNodes) {
        // merge nodes
        List<Integer> newNeighbours = Stream.concat(node1.getNeighborsId().stream(), node2.getNeighborsId().stream())
                .filter(id -> !Objects.equals(id, node1.getId()) && !Objects.equals(id, node2.getId()))
                .distinct().collect(Collectors.toList());

        Node newNode = new Node(newPosition, newNeighbours);
        // update all neighbours
        for(int neighbourId : newNeighbours) {
            Node neighbour = this.nodes.get(neighbourId);
            neighbour.addNeighbor(newNode.getId());
        }

        positionsAccessibleByNodeId.put(newNode.getId(), positionsAccessibleByBothNodes);
        positionsAccessibleByNodeId.remove(node1.getId());
        positionsAccessibleByNodeId.remove(node2.getId());
        this.nodes.put(newNode.getId(), newNode);

        removeNode(node1);
        removeNode(node2);
    }

    private boolean tryToMerge(Node node1, Node node2, Map<Integer, List<Vector>> positionsAccessibleByNodeId) {
        if(!isPositionVisibleWithinWalls(node1.getPosition(), node2.getPosition(), this.walls))
            return false;
        Vector middlePoint = node1.getPosition()
                .add(node2.getPosition().subtract(node1.getPosition()).scalarMultiply(0.5));
        List<Vector> positionsAccessibleByBothNodes = Stream.concat(
                positionsAccessibleByNodeId.get(node1.getId()).stream(),
                positionsAccessibleByNodeId.get(node2.getId()).stream()
        ).distinct().collect(Collectors.toList());

        for (Vector target : positionsAccessibleByBothNodes) {
            if(!isPositionVisibleWithinWalls(middlePoint, target, this.walls)) {
                return false; // CANT BE MERGED
            }
        }

        merge(node1, node2, middlePoint, positionsAccessibleByNodeId, positionsAccessibleByBothNodes);
        return true;
    }

    private void removeNode(Node node) {
        for(int neighbourId : node.getNeighborsId()) {
            Node neighbour = this.nodes.get(neighbourId);
            neighbour.removeNeighbor(node.getId());
        }
        this.nodes.remove(node.getId());
    }

    private void reduceGraph(List<Wall> walls, List<Vector> accessiblePositions) {
        // we need to know to which targets/Servers each node has access to, in order to check later that the reduced nodes has access to them to
        Map<Integer, List<Vector>> positionsAccessibleByNodeId = new HashMap<>();
        this.nodes.forEach((id, node) -> {
            List<Vector> positionsAccessible = new ArrayList<>();
            for (Vector pos : accessiblePositions) {
                if(isPositionVisibleWithinWalls(node.getPosition(), pos, walls))
                    positionsAccessible.add(pos);
            }
            positionsAccessibleByNodeId.put(id, positionsAccessible);
        });

        // Iterate with BFS, when possible merge found, do it and restart
        HashSet<Integer> visitedIds = new HashSet<>();
        LinkedList<Integer> queue = new LinkedList<>();
        int removedNodes = -1;
        boolean hasBeenMerged = true;
        Node current;
        while (hasBeenMerged) {
            removedNodes++;
            hasBeenMerged = false;
            int firstId = this.nodes.values().stream().findFirst().get().getId();
            queue.clear();
            queue.add(firstId);
            visitedIds.clear();

            while (queue.size() != 0 && !hasBeenMerged) {
                current = this.nodes.get(queue.poll());
                visitedIds.add(current.getId());

                for (int neighbour : current.getNeighborsId()) {
                    if(!visitedIds.contains(neighbour)) {
                        if(tryToMerge(current, this.nodes.get(neighbour), positionsAccessibleByNodeId)) {
                            //merged, restart BFS
                            hasBeenMerged = true;
                            break;
                        }
                        queue.add(neighbour);
                    }
                }
            }
        }
        System.out.println("\t\t" + removedNodes + " nodes of " + (this.nodes.size() + removedNodes) + " have been removed.");
    }

    private Vector getMirroredPosition(Wall line, Vector position) {
        // mirrored point https://stackoverflow.com/questions/8954326/how-to-calculate-the-mirror-point-along-a-line#:~:text=px%27%2C%20py%27).-,Alternatively,-%2C%20you%20can%20use

        double A = line.getB().getY() - line.getA().getY();
        double B = -(line.getB().getX() - line.getA().getX());
        double C = -A * line.getA().getX() - B * line.getA().getY();

        // normalize
        double M = Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
        A /= M;
        B /= M;
        C /= M;

        double D = A * position.getX() + B * position.getY() + C;
        double mirrorX = position.getX() - 3 * A * D;
        double mirrorY = position.getY() - 3 * B * D;

        return new Vector(mirrorX, mirrorY);
    }

    public Vector getClosestDestination(Vector fromPosition, List<Vector> possibleDestinations, double radius) {
        double minDistance = Double.MAX_VALUE;
        Vector closestDestination = null;
        for (Vector possibleDestination : possibleDestinations) {
            NodePath path = this.getPathToPosition(fromPosition, possibleDestination, radius);
            if (path != null) {
                double pathTotalDistance;
                if (path.getFirstNode() != null)
                    // path has at least one node
                    pathTotalDistance = fromPosition.distance(path.getFirstNode().getPosition()) + path.getDistance() +
                            path.getLastNode().getPosition().distance(possibleDestination);
                else pathTotalDistance = fromPosition.distance(possibleDestination);

                if (minDistance > pathTotalDistance) {
                    minDistance = pathTotalDistance;
                    closestDestination = possibleDestination;
                }
            } else
                System.out.println("Possible destination inaccessible");
        }

        return closestDestination;
    }

    private NodePath pathReducer(Vector fromPosition, Vector toPosition, NodePath path, double radius) {
        Node currentNode = path.getFirstNode();
        if (currentNode == null)
            // path has 0 nodes between ends
            return path;

        Node prevNode = currentNode;
        // keep last visible node from fromPosition
        while (currentNode != null && this.isPositionAccessible(fromPosition, currentNode.getPosition(), radius)) {
            prevNode = currentNode;
            currentNode = path.getNodeAfter(prevNode);
        }
        NodePath reducedPath = new NodePath(prevNode);

        currentNode = prevNode; // reset current
        // keep only essential in between nodes  (erase those that are between nodes (and toPosition) that can see each other)
        while (!isPositionAccessible(currentNode.getPosition(), toPosition, radius)) {
            prevNode = currentNode;
            currentNode = path.getNodeAfter(currentNode);

            // current node is not visible from last saved node, save the previous one that is neighbour of current, so it can see it
            if (!isPositionAccessible(reducedPath.getLastNode().getPosition(), currentNode.getPosition(), radius))
                reducedPath.add(prevNode);
        }
        // case where last visible node from fromPosition is the first node that is visible from toPosition
        if (!reducedPath.getLastNode().equals(currentNode))
            reducedPath.add(currentNode);

        return reducedPath;
    }

    public NodePath AStar(Node from, Vector to, double radius) {
        PriorityQueue<NodePath> frontierPaths = new PriorityQueue<>(Comparator.comparingDouble(NodePath::getFunctionValue));
        Node currentNode = from;
        NodePath currentPath = new NodePath(from);

        HashMap<Integer, Double> visitedNodesIdWithMinWeight = new HashMap<>();
        visitedNodesIdWithMinWeight.put(currentNode.getId(), currentPath.getFunctionValue()); // root

        while (!isPositionAccessible(currentNode.getPosition(), to, radius)) {
            // once target is visible from the last node of the path, return it

            for (Integer nextNodeId : currentNode.getNeighborsId()) {
                double newPathDistance = currentPath.getDistanceValueAdding(this.nodes.get(nextNodeId));
                if (!visitedNodesIdWithMinWeight.containsKey(nextNodeId) || visitedNodesIdWithMinWeight.get(nextNodeId) > newPathDistance) {
                    // expand on new node or when a better path is found
                    frontierPaths.add(currentPath.copyAndAdd(this.nodes.get(nextNodeId)));
                    visitedNodesIdWithMinWeight.put(nextNodeId, newPathDistance);
                }
            }

            if (frontierPaths.size() == 0) {
                System.err.println("NO PATH FOUND BETWEEN " + from.getPosition() + " and " + to);
                return null; // no path found
            }

            currentPath = frontierPaths.poll();
            currentNode = currentPath.getLastNode();
        }

        return currentPath;
    }

    public void generateOutput(String outputPath) {
        System.out.println("\tCreating static file. . .");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(outputPath + graphBackupFileName, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        writer.write("AGENT_ID,POSITION_X,POSITION_Y,NEIGHBOURS*\n");
        for (Node node : this.nodes.values()) {
            StringBuilder neighborsIds = new StringBuilder();
            node.getNeighborsId().forEach(neighborId -> neighborsIds.append(String.format(Locale.ENGLISH, "%d,", neighborId)));
            writer.write(String.format(Locale.ENGLISH, "%d,%s,%s,%s\n", node.getId(), node.getPosition().getX(), node.getPosition().getY(), neighborsIds));
        }

        writer.close();
        try {
            writer = new PrintWriter(outputPath + dxfDataFileName, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        writer.write(this.dxfName + "\n");
        writer.close();
        System.out.println("\tStatic file successfully created");
    }
}
