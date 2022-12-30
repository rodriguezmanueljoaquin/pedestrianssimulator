package Agent;

import AgentsBehaviour.StateMachine.StateMachine;
import Environment.Objectives.Objective;
import GraphGenerator.Node;
import GraphGenerator.NodePath;
import Utils.Constants;
import Utils.Vector;

import java.util.List;
import java.util.Objects;

public class Agent {
    private Vector position;
    private Vector velocity;
    private final StateMachine stateMachine;
    private static Integer count = 1;
    private final Integer id;
    private double radius;
    private AgentStates state;
    private List<? extends Objective> objectives;
    private Double startedAttendingAt;
    private NodePath currentPath;
    private Node currentIntermediateObjectiveNode;

    public Agent(Vector x, double radius, StateMachine stateMachine, List<? extends Objective> objectives) {
        this.position = x;
        this.velocity = new Vector(0, 0);
        this.radius = radius;
        this.stateMachine = stateMachine;
        this.state = AgentStates.STARTING; //just started
        this.id = count++;
        this.objectives = objectives;
    }

    public void updateVelocity() {
        if (state == AgentStates.LEAVING) {
            // will be destroyed next iteration
            this.setVelocity(new Vector(0, 0));
            return;
        }

        // first check if intermediate node has to be updated
        if (currentIntermediateObjectiveNode != null &&
                this.position.distance(this.currentIntermediateObjectiveNode.getPosition()) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
            // intermediate node reached, update it
            this.currentIntermediateObjectiveNode = this.currentPath.getNodeAfter(this.currentIntermediateObjectiveNode);
        }

        Vector objectivePosition;
        if (currentIntermediateObjectiveNode == null)
            // go to objective because it is visible, there are no remaining intermediate objectives
            objectivePosition = this.getCurrentObjective().getPosition(this);
        else {
            objectivePosition = this.currentIntermediateObjectiveNode.getPosition();
        }

        Vector r = objectivePosition.substract(this.position).normalize();
        this.setVelocity(r.scalarMultiply(this.getState().getVelocity()));
    }

    public void updatePosition(double time) {
        this.position = this.position.add(this.velocity.scalarMultiply(time));
    }

    public boolean reachedObjective() {
        return this.getPosition().distance(this.getCurrentObjective().getPosition(this)) < Constants.MINIMUM_DISTANCE_TO_TARGET;
    }

    public Objective getCurrentObjective() {
        if (hasObjectives())
            return objectives.get(0);
        return null;
    }

    public Objective popNextObjective() {
        objectives.remove(0);
        return getCurrentObjective();
    }

    public NodePath getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(NodePath currentPath) {
        this.currentIntermediateObjectiveNode = currentPath.getFirstNode();
        this.currentPath = currentPath;
    }

    public boolean hasObjectives() {
        return objectives.size() > 0;
    }

    public Double getStartedAttendingAt() {
        return startedAttendingAt;
    }

    public void setStartedAttendingAt(Double startedAttendingAt) {
        this.startedAttendingAt = startedAttendingAt;
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public double distance(Agent other) {
        return this.getPosition().distance(other.position) - this.radius - other.radius;
    }

    public Integer getId() {
        return this.id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Vector getPosition() {
        return this.position;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vector speed) {
        this.velocity = speed;
    }

    public AgentStates getState() {
        return state;
    }

    public void setState(AgentStates state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return Objects.equals(id, agent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

