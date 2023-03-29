package GraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// necessary for AStar heuristic
public class NodePath {
    private final List<Node> path;
    private Double functionValue;
    private Double distance;

    public NodePath() {
        this.path = new ArrayList<>();
        this.functionValue = 0.;
        this.distance = 0.;
    }

    public NodePath(Node firstNode) {
        this.path = new ArrayList<>();
        this.path.add(firstNode);
        this.functionValue = 0.;
        this.distance = 0.;
    }

    public NodePath(List<Node> path, double functionValue, double distance) {
        this.path = path;
        this.functionValue = functionValue;
        this.distance = distance;
    }

    public void add(Node node) {
        if (!node.equals(this.getLastNode())) {
            // avoid repetition
            this.path.add(node);
            this.distance += this.path.get(this.path.size()-2).getPosition().distance(node.getPosition());
            this.functionValue = this.distance; // TODO: FALTA HEURISTICA, AHORA SOLO CONSIDERA COSTO (Fn = Cn)
        }
    }

    public NodePath copyAndAdd(Node node) {
        NodePath answer = this.copy();
        answer.add(node);
        return answer;
    }

    public NodePath copy() {
        return new NodePath(new ArrayList<>(this.path), this.functionValue, this.distance);
    }

    public Node getLastNode() {
        int size = this.path.size();
        if (size == 0)
            return null;
        return this.path.get(size - 1);
    }

    public Node getFirstNode() {
        if (this.path.size() == 0)
            return null;

        return this.path.get(0);
    }

    public Node next() {
        if (this.path.size() == 0)
            return null;
        return this.path.remove(0);
    }

    public Node getNodeAfter(Node node) {
        int index = this.path.indexOf(node);
        if (index == -1)
            throw new RuntimeException("Nonexistent node in path");
        else if (index == this.path.size() - 1) {
            return null;
        }

        return this.path.get(index + 1);
    }

    public Double getFunctionValue() {
        return this.functionValue;
    }

    public Double getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        for (Node node : this.path) {
            answer.append(node.getId() + ";");
        }
        if (answer.length() > 0)
            // remove last delimiter
            answer.deleteCharAt(answer.length() - 1);
        return answer.toString();
    }
}
