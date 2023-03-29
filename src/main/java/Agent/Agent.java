package Agent;

import AgentsBehaviour.StateMachine.StateMachine;
import Environment.Objectives.Exit;
import Environment.Objectives.Objective;
import GraphGenerator.Node;
import GraphGenerator.NodePath;
import Utils.Vector;

import java.util.List;
import java.util.Objects;

public class Agent {
    private static Integer count = 1;
    private final StateMachine stateMachine;
    private final Integer id;
    private final double maxRadius;
    private final double minRadius;
    private final double maxVelocity;
    private List<Objective> objectives;
    private Vector position;
    private Vector direction;
    private double radius;
    private AgentStates state;
    private Double startedAttendingAt;
    private NodePath currentPath;
    private Node currentIntermediateObjectiveNode;

    public Agent(Vector x, double minRadius, double maxRadius, double maxVelocity, StateMachine stateMachine, List<Objective> objectives) {
        this.position = x;
        this.direction = new Vector(0, 1);

        if (maxRadius < 0)
            this.maxRadius = 0.5;
        else this.maxRadius = maxRadius;
        if (minRadius > this.maxRadius || minRadius < 0) {
            this.minRadius = maxRadius * 0.95;
        } else this.minRadius = minRadius;

        this.radius = maxRadius;
        this.maxVelocity = maxVelocity;
        this.stateMachine = stateMachine;
        this.state = AgentStates.STARTING; //just started
        this.id = count++;
        this.objectives = objectives;
    }

    public void updateDirection() {
        if (this.getState() == AgentStates.LEAVING) {
            return;
        }
        // first check if intermediate node has to be updated
        if (this.currentIntermediateObjectiveNode != null && this.reachedPosition(this.currentIntermediateObjectiveNode.getPosition())) {
            // intermediate node reached, update it
            this.currentIntermediateObjectiveNode = this.currentPath.next();
        }

        Vector objectivePosition;
        if (this.currentIntermediateObjectiveNode == null)
            // go to objective because it is visible, there are no remaining intermediate objectives
            objectivePosition = this.getCurrentObjective().getPosition(this);
        else {
            objectivePosition = this.currentIntermediateObjectiveNode.getPosition();
        }
        Vector r = objectivePosition.subtract(this.getPosition()).normalize();
        this.setDirection(r);
    }

    public void updatePosition(double deltaTime) {
        this.position = this.position.add(this.getVelocity().scalarMultiply(deltaTime));
    }

    public boolean reachedPosition(Vector position) {
        return this.distance(position) <= 0;
    }

    public Node getIntermediateObjectiveNode() {
        return this.currentIntermediateObjectiveNode;
    }

    public Objective getCurrentObjective() {
        if (hasObjectives())
            return this.objectives.get(0);
        return null;
    }

    public Objective popNextObjective() {
        this.objectives.remove(0);
        return getCurrentObjective();
    }

    public NodePath getCurrentPath() {
        return this.currentPath;
    }

    public void setCurrentPath(NodePath currentPath) {
        this.currentIntermediateObjectiveNode = currentPath.getFirstNode();
        this.currentPath = currentPath;
    }

    public boolean hasObjectives() {
        return objectives.size() > 0;
    }

    public Double getStartedAttendingAt() {
        return this.startedAttendingAt;
    }

    public void setStartedAttendingAt(Double startedAttendingAt) {
        this.startedAttendingAt = startedAttendingAt;
    }

    public StateMachine getStateMachine() {
        return this.stateMachine;
    }

    public double distance(Agent other) {
        return this.getPosition().distance(other.getPosition()) - this.getRadius() - other.getRadius();
    }

    public double distance(Vector position) {
        return this.getPosition().distance(position) - this.getRadius();
    }

    public void evacuate(List<Exit> exits) {
        if (this.objectives.size() > 1) {
            //is not going to exit
            this.stateMachine.evacuate(this, exits);
        }
    }

    public void setObjectives(List<Objective> objectives) {
        this.objectives = objectives;
    }

    public Integer getId() {
        return this.id;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Vector getPosition() {
        return this.position;
    }

    public Vector getDirection() {
        return this.direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getVelocity() {
        return this.direction.scalarMultiply(this.getVelocityModule());
    }

    public double getVelocityModule() {
        double currentMaxVelocity = this.getState().getMaxVelocityFactor() * this.maxVelocity;
        double currentVelocity = currentMaxVelocity * (Math.pow((this.getRadius() - (this.minRadius * 0.97)) /
                (this.maxRadius - this.minRadius), AgentConstants.B));
        // subtract a little from min radius in the nominator in order to avoid complete freeze of the agent,
        // this can cause velocity to be over max. We catch such cause returning the min between both.
        return Math.min(currentVelocity, currentMaxVelocity);
    }

    public double getMaxRadius() {
        return this.maxRadius;
    }

    public double getMinRadius() {
        return this.minRadius;
    }

    public AgentStates getState() {
        return this.state;
    }

    public void setState(AgentStates state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return Objects.equals(this.getId(), agent.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}

