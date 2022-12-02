package GraphGenerator;

import Environment.Wall;
import Utils.Vector;

import java.awt.geom.Line2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Graph {
    private final static double STEP_SIZE = .1;

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
        for(Wall wall : this.walls)
            if(wall.intersectsLine(origin, destiny))
                return false;

        return true;
    }

    // initialPosition has to be a valid position, from this node the graph will expand
    public void generateGraph(Vector initialPosition) {
        Node root = new Node(initialPosition);
        this.nodes.put(initialPosition, root);
        List<Node> notVisitedNodes = new ArrayList<>();
        notVisitedNodes.add(root);

        while (!notVisitedNodes.isEmpty()) {
            System.out.println(notVisitedNodes.size());
            Node current = notVisitedNodes.remove(0);
            List<Node> currentNeighbours = new ArrayList<>();
            Vector startPosition = current.getPosition();

            for(Vector difference : POSSIBLE_NEIGHBORS_POSITION_DIFFERENCE) {
                Vector possibleNeighbourPosition = difference.add(startPosition);

                if(this.nodes.containsKey(possibleNeighbourPosition)) {
                    // position already visited (which assures that it is valid)
                    currentNeighbours.add(this.nodes.get(possibleNeighbourPosition));
                } else if(isPositionVisible(startPosition, possibleNeighbourPosition)) {
                    // position not visited and valid
                    Node newNode = new Node(possibleNeighbourPosition);
                    currentNeighbours.add(newNode);
                    this.nodes.put(possibleNeighbourPosition, newNode);

                    notVisitedNodes.add(newNode);
                }
            }

            current.addNeighbors(currentNeighbours);
        }

    }

    public void generateOutput(String outputPath) {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputPath+ "graph.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        for (Node node : this.nodes.values()) {
            StringBuilder neighboursIds = new StringBuilder("");
            node.getNeighbors().forEach(node1 -> neighboursIds.append(String.format(Locale.ENGLISH, "%d;", node1.id)));
            writer.write(String.format(Locale.ENGLISH, "%d;%s;%s\n", node.id, node.getPosition().toString(), neighboursIds));
        }

        writer.close();
        System.out.println("\tStatic file successfully created");
    }
}
