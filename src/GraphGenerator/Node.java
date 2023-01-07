package GraphGenerator;

import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private static Integer count = 1;
    private final Vector position;
    private final List<Node> neighbors;
    private final Integer id;

    public Node(Vector position) {
        this.position = position;
        this.neighbors = new ArrayList<>();
        this.id = count++;
    }

    public Vector getPosition() {
        return position;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public Integer getId() {
        return id;
    }

    public void addNeighbors(List<Node> nodes) {
        for (Node node : nodes)
            if (!this.neighbors.contains(node))
                this.neighbors.add(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(position, node.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
