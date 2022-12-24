package GraphGenerator;

import Environment.Wall;
import Utils.Vector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Graph {
    private final static double STEP_SIZE = 2;
//    private static Graph instance;

    // CLOCKWISE
    private final static Vector[] POSSIBLE_NEIGHBORS_POSITION_DIFFERENCE = {
            new Vector(0., STEP_SIZE),
            new Vector(STEP_SIZE, 0.),
            new Vector(0., -STEP_SIZE),
            new Vector(-STEP_SIZE, 0.),

    };

    // Map because we avoid recreating nodes on positions (key in map) already visited
    private Map<Vector, Node> nodes;
    private List<Wall> walls;

    public Graph(List<Wall> walls) {
        this.nodes = new HashMap<>();
        this.walls = walls;
    }

    public Map<Vector, Node> getNodes() {
        return nodes;
    }

    public boolean isPositionVisible(Vector origin, Vector destiny) {
        for (Wall wall : this.walls)
            if (wall.intersectsLine(origin, destiny) && !wall.contains(destiny))
                // wall intersects the path from origin and destiny, and destiny is not in the wall
                return false;

        return true;
    }

    public Node getClosestVisibleNode(Vector position) {
        Node bestNode = null;
        double minDistance = Double.MAX_VALUE;
        double currentDistance;
        for (Node node : this.nodes.values()) {
            currentDistance = node.getPosition().distance(position);
            if (currentDistance < minDistance) {
                bestNode = node;
                minDistance = currentDistance;
            }
        }
        if (bestNode == null)
            throw new RuntimeException();
        return bestNode;
    }

    public double getPathLengthToObjective(Vector fromPostion, Vector toPosition) {
        NodePath path = getPathToPosition(fromPostion, toPosition);
        Node currentNode = path.getFirstNode();
        Node lastNode = path.getLastNode();
        Node previousNode;
        double sum = 0.0;
        while (currentNode != lastNode) {
            previousNode = currentNode;
            currentNode = path.getNodeAfter(currentNode);
            sum += previousNode.getPosition().distance(currentNode.getPosition());
        }
        return sum;
    }

    public NodePath getPathToPosition(Vector fromPosition, Vector toPosition) {
        // first try to get by current position, otherwise get the closest visible
        Node fromNode = nodes.get(fromPosition);
        if (fromNode == null) {
            fromNode = getClosestVisibleNode(fromPosition);
        }
        NodePath fullPath = AStar(fromNode, toPosition);
        if (fullPath == null) {
            // none path found
            return null;
        }

        return pathReducer(fromPosition, toPosition, fullPath);
    }

    // initialPosition has to be a valid position, from this node the graph will expand
    public void generateGraph(Vector initialPosition) {
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
                    if (isPositionVisible(startPosition, possibleNeighbourPosition))
                        currentNeighbours.add(neighbor);

                } else if (isPositionVisible(startPosition, possibleNeighbourPosition)) {
                    // position not considered and valid
                    neighbor = new Node(possibleNeighbourPosition);
                    this.nodes.put(possibleNeighbourPosition, neighbor);

                    currentNeighbours.add(neighbor);
                    notVisitedNodes.add(neighbor);
                }
            }

            current.addNeighbors(currentNeighbours);
        }
    }

    private NodePath pathReducer(Vector fromPosition, Vector toPosition, NodePath path) {
        Node currentNode = path.getFirstNode();
        if (currentNode == null)
            // path has 0 nodes between ends
            return path;

        Node prevNode = currentNode;
        NodePath reducedPath = new NodePath();

        // keep last visible node from fromPosition
        while (currentNode != null && isPositionVisible(fromPosition, currentNode.getPosition())) {
            prevNode = currentNode;
            currentNode = path.getNodeAfter(prevNode);
        }
        reducedPath.add(prevNode);

        currentNode = prevNode; // reset current
        // keep only essential in between nodes  (erase those that are between nodes (and toPosition) that can see each other)
        while (!isPositionVisible(currentNode.getPosition(), toPosition)) {
            prevNode = currentNode;
            currentNode = path.getNodeAfter(currentNode);

            // current node is not visible from last saved node, save the previous one that is neighbour of current so it can see it
            if (!isPositionVisible(reducedPath.getLastNode().getPosition(), currentNode.getPosition()))
                reducedPath.add(prevNode);
        }
        // case where last visible node from fromPosition is the first node that is visible from toPosition
        if (!reducedPath.getLastNode().equals(currentNode))
            reducedPath.add(currentNode);

        return reducedPath;
    }

    public NodePath AStar(Node from, Vector to) {
        PriorityQueue<NodePath> frontierPaths = new PriorityQueue<>(Comparator.comparingDouble(NodePath::getFunctionValue));
        Node currentNode = from;
        NodePath currentPath = new NodePath();
        currentPath.add(from);

        HashSet<Integer> visitedNodesId = new HashSet<>();

        while (!isPositionVisible(currentNode.getPosition(), to)) {
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

    public void generateOutput(String outputPath) {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputPath + "graph.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        for (Node node : this.nodes.values()) {
            StringBuilder neighboursIds = new StringBuilder("");
            node.getNeighbors().forEach(node1 -> neighboursIds.append(String.format(Locale.ENGLISH, "%d;", node1.getId())));
            writer.write(String.format(Locale.ENGLISH, "%d;%s;%s\n", node.getId(), node.getPosition().toString(), neighboursIds));
        }

        writer.close();
        System.out.println("\tStatic file successfully created");
    }
}
