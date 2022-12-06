package GraphGenerator;

import java.util.ArrayList;
import java.util.List;

// necessary for AStar heuristic
public class NodePath {
    private List<Node> path;
    private Double functionValue;

    public NodePath() {
        this.path = new ArrayList<>();
        recalculateFunctionValue();
    }

    public void add(Node node) {
        this.path.add(node);
        recalculateFunctionValue();
    }

    public void addAll(List<Node> nodes) {
        this.path.addAll(nodes);
        recalculateFunctionValue();
    }

    public Node getLastNode() {
        return this.path.get(this.path.size()-1);
    }

    public Node getFirstNode() {
        return this.path.get(0);
    }
    public NodePath copyAndAdd(Node node) {
        NodePath answer = new NodePath();
        answer.addAll(this.path);
        answer.add(node);
        return answer;
    }

    private void recalculateFunctionValue() {
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += path.get(i).getPosition().distance(path.get(i+1).getPosition());
        }

        this.functionValue =  totalDistance; // TODO: FALTA HEURISITCA, AHORA SOLO CONSIDERA COSTO
    }

    public Node getNodeAfter(Node node) {
        int index = this.path.indexOf(node);
        if(index == -1)
            throw new RuntimeException("Nonexistent node in path");
        else if (index == this.path.size() - 1) {
            return null;
        }

        return this.path.get(index+1);
    }

    public Double getFunctionValue() {
        return functionValue;
    }

    @Override
    public String toString() {
        StringBuilder answer = new StringBuilder();
        for (Node node: this.path) {
            answer.append(node.getId() + ";");
        }
        // remove last delimiter
        answer.deleteCharAt(answer.length()-1);
        return answer.toString();
    }
}
