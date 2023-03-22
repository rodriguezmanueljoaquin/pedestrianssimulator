package GraphGenerator;

import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            this.addNeighbor(node);
    }

    public void addNeighbor(Node node) {
        if (!this.neighbors.contains(node))
            this.neighbors.add(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{" +
                "position=" + position +
                ", neighbors=" + neighbors.stream().map(Node::getId).collect(Collectors.toList()) +
                ", id=" + id +
                '}';
    }
}
