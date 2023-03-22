package GraphGenerator;

import java.util.ArrayList;
import java.util.List;

// necessary for AStar heuristic
public class NodePath {
    private final List<Node> path;
    private Double functionValue;
    private Double distance;

    public NodePath() {
        this.path = new ArrayList<>();
        recalculateFunctionValueAndDistance();
    }

    public void add(Node node) {
        if(!node.equals(this.getLastNode())) {
            // avoid repetition
            this.path.add(node);
            recalculateFunctionValueAndDistance();
        }
    }

    public NodePath copyAndAdd(Node node) {
        NodePath answer = this.copy();
        answer.add(node);
        return answer;
    }

    public NodePath copy() {
        NodePath answer = new NodePath();
        for (Node copyNode: this.path) {
            answer.add(copyNode);
        }
        return answer;
    }

    public Node getLastNode() {
        if (this.path.size() == 0)
            return null;
        return this.path.get(this.path.size() - 1);
    }

    public Node getFirstNode() {
        if (this.path.size() == 0)
            return null;

        return this.path.get(0);
    }

    private void recalculateFunctionValueAndDistance() {
        double totalDistance = 0;
        for (int i = 0; i < this.path.size() - 1; i++) {
            totalDistance += this.path.get(i).getPosition().distance(this.path.get(i + 1).getPosition());
        }

        this.distance = totalDistance;
        this.functionValue = this.distance; // TODO: FALTA HEURISITCA, AHORA SOLO CONSIDERA COSTO
    }

    public Node next() {
        if(this.path.size() == 0)
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

        return this.path.get(index+1);
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
        if(answer.length() > 0)
            // remove last delimiter
            answer.deleteCharAt(answer.length() - 1);
        return answer.toString();
    }
}
