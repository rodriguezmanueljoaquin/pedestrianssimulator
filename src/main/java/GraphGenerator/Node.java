package GraphGenerator;

import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
    private static Integer count = 1;
    private final Vector position;
    private final List<Integer> neighborsId;
    private final Integer id;

    public Node(Vector position) {
        this.position = position;
        this.neighborsId = new ArrayList<>();
        this.id = count++;
    }

    public Node(Integer id, Vector position, List<Integer> neighborsId) {
        this.position = position;
        this.neighborsId = neighborsId;
        this.id = id;
    }

    public Node(Vector position, List<Integer> neighborsId) {
        this.position = position;
        this.neighborsId = neighborsId;
        this.id = count++;
    }

    public Vector getPosition() {
        return position;
    }

    public List<Integer> getNeighborsId() {
        return neighborsId;
    }

    public Integer getId() {
        return id;
    }

    public void addNeighbors(List<Node> nodes) {
        for (Node node : nodes)
            this.addNeighbor(node.getId());
    }

    public void addNeighbor(Integer nodeId) {
        if (!this.neighborsId.contains(nodeId))
            this.neighborsId.add(nodeId);
    }

    public void removeNeighbor(Integer nodeId) {
        this.neighborsId.remove(nodeId);
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
                ", neighbors=" + neighborsId +
                ", id=" + id +
                '}';
    }
}
