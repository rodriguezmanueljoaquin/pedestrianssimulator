package GraphGenerator;

import Environment.Wall;
import Utils.Vector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Predicate;

public class Graph {
    private final static double STEP_SIZE = 1.8; // Found empirically, TODO: AUTOMATIZE STEP SIZE SELECTION

    // CLOCKWISE
    private final static Vector[] POSSIBLE_NEIGHBORS_POSITION_DIFFERENCE = {
            new Vector(0., STEP_SIZE),
            new Vector(STEP_SIZE, 0.),
            new Vector(0., -STEP_SIZE),
            new Vector(-STEP_SIZE, 0.),

    };

    // Map because we avoid recreating nodes on positions (key in map) already visited
    private final Map<Vector, Node> nodes;
    private final List<Wall> walls;

    public Graph(List<Wall> walls, List<Wall> exits, Vector initialPosition) {
        this.nodes = new HashMap<>();
        this.walls = walls;
        System.out.println("\tGenerating graph.");
        this.generateGraph(initialPosition, exits);
        System.out.println("\tGraph generated.");
    }

    public boolean isPositionAccessible(Vector origin, Vector destiny, double radius) {
        // checks if whole circumference of the circle can access to destiny
        // for this, picks "highest" point in circumferences and checks if it can see the "highest" point in destination
        // with "highest", we refer to the perpendicular point to the distance vector in the circumference
        Vector distance = destiny.substract(origin);
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

        // first try to get by current position, otherwise get the closest accessible
        Node fromNode = this.nodes.get(fromPosition);
        if (fromNode == null) {
            fromNode = this.getClosestAccessibleNode(fromPosition, radius);
        }

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
        this.nodes.put(initialPosition, root);

        List<Node> notVisitedNodes = new ArrayList<>();
        notVisitedNodes.add(root);

        while (!notVisitedNodes.isEmpty()) {
            Node current = notVisitedNodes.remove(0);
            List<Node> currentNeighbours = new ArrayList<>();
            Vector startPosition = current.getPosition();

            for (Vector difference : POSSIBLE_NEIGHBORS_POSITION_DIFFERENCE) {
                Vector possibleNeighbourPosition = difference.add(startPosition);

                Node neighbor;
                if (this.nodes.containsKey(possibleNeighbourPosition)) {
                    // position already considered, is not necessary to create the node
                    neighbor = this.nodes.get(possibleNeighbourPosition);

                    // check if its visible
                    if (isPositionVisibleWithinWalls(startPosition, possibleNeighbourPosition, wallsToConsider))
                        currentNeighbours.add(neighbor);

                } else if (isPositionVisibleWithinWalls(startPosition, possibleNeighbourPosition, wallsToConsider)) {
                    // position not considered and valid
                    neighbor = new Node(possibleNeighbourPosition);
                    this.nodes.put(possibleNeighbourPosition, neighbor);

                    currentNeighbours.add(neighbor);
                    notVisitedNodes.add(neighbor);
                }
            }

            current.addNeighbors(currentNeighbours);
        }

        // add nodes on exits for agents that summon outside the establishment
        // and add an extra mirrored node outside just in case
        for (Wall wall : extraWalls) {
            Node node = new Node(wall.getCentroid());
            Node closestNode = this.getClosestVisibleNode(node.getPosition());
            node.addNeighbor(closestNode);
            closestNode.addNeighbor(node);
            this.nodes.put(node.getPosition(), node);

            Node mirrorNode = new Node(getMirroredPosition(wall, closestNode.getPosition()));
            node.addNeighbor(mirrorNode);
            mirrorNode.addNeighbor(node);
            this.nodes.put(mirrorNode.getPosition(), mirrorNode);
        }
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
        double mirrorX = position.getX() - 2 * A * D;
        double mirrorY = position.getY() - 2 * B * D;

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
        NodePath reducedPath = new NodePath();

        // keep last visible node from fromPosition
        while (currentNode != null && isPositionAccessible(fromPosition, currentNode.getPosition(), radius)) {
            prevNode = currentNode;
            currentNode = path.getNodeAfter(prevNode);
        }
        reducedPath.add(prevNode);

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
        NodePath currentPath = new NodePath();
        currentPath.add(from);

        HashSet<Integer> visitedNodesId = new HashSet<>();

        while (!isPositionAccessible(currentNode.getPosition(), to, radius)) {
            // once target is visible from the last node of the path, return it
            visitedNodesId.add(currentNode.getId());

            for (Node next : currentNode.getNeighbors()) {
                if (!visitedNodesId.contains(next.getId())) {
                    frontierPaths.add(currentPath.copyAndAdd(next));
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

    // Method for showing the node positions (DEBUGGING)
    public void generateOutput(String outputPath) {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputPath + "graph.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        for (Node node : this.nodes.values()) {
            StringBuilder neighboursIds = new StringBuilder();
            node.getNeighbors().forEach(node1 -> neighboursIds.append(String.format(Locale.ENGLISH, "%d;", node1.getId())));
            writer.write(String.format(Locale.ENGLISH, "%d;%s;%s\n", node.getId(), node.getPosition().toString(), neighboursIds));
        }

        writer.close();
        System.out.println("\tStatic file successfully created");
    }
}
