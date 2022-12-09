package GraphGenerator;

import Agent.Agent;
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

    private boolean isPositionVisible(Vector origin, Vector destiny) {
        for (Wall wall : this.walls)
            if (wall.intersectsLine(origin, destiny))
                return false;

        return true;
    }

    //Cambios que hice:
    //El agente tiene un nodePath que sigue, el resto se hace desde
    //el grafo, o sea la logica seria:
    //  Desde Simulation/State machine le damos un objetivo y un nodePath al agente

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

    public NodePath getPathToObjective(Agent agent) {
        // first try to get by current position, otherwise get the closest visible
        Node fromNode = nodes.get(agent.getPosition());
        if (fromNode == null) {
            fromNode = getClosestVisibleNode(agent.getPosition());
        }
        //Checkear en la state machine de no mandarle null!
        return AStar(fromNode, agent.getCurrentObjective().getPosition());
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
                    if(isPositionVisible(startPosition, possibleNeighbourPosition))
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

            if (frontierPaths.size() == 0)
                return null;

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
