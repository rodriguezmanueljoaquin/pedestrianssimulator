package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentStates;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;
import Utils.Constants;

public class StudentSM implements StateMachine {
    private final Graph graph;

    public StudentSM(Graph graph) {
        this.graph = graph;
    }

    private void updateAgentCurrentObjective(Agent agent) {
        if (agent.hasObjectives()) {
            NodePath path = this.graph.getPathToPosition(agent.getPosition(), agent.getCurrentObjective().getPosition(agent));

            if (path == null) {
                // FIXME! Checkear por que a veces da null
                System.out.println("ERROR: NULL searching for path to objective, from: " + agent.getPosition() + " to: " + agent.getCurrentObjective().getPosition(agent));
                agent.setState(AgentStates.LEAVING);
            } else {
                agent.setCurrentPath(path);
                agent.setState(AgentStates.MOVING);
            }
        } else {
            agent.setState(AgentStates.LEAVING);
        }
    }

    @Override
    public void updateAgent(Agent agent, double currentTime) {
        switch (agent.getState()) {
            case MOVING:
                if(agent.getCurrentObjective().isServer() &&
                        !graph.isPositionVisible(agent.getCurrentPath().getLastNode().getPosition(),agent.getCurrentObjective().getPosition(agent)))
                    // Server may change the position the agent has to go to while he is moving, therefore agent should update its path accordingly
                    updateAgentCurrentObjective(agent);

                if (agent.getPosition().distance(agent.getCurrentObjective().getPosition(agent)) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
                    if (agent.getCurrentObjective().canAttend(agent)) {
                        agent.setStartedAttendingAt(currentTime);
                        agent.setState(AgentStates.ATTENDING);
                    } else {
                        // agent has to wait until objective can attend him
                        agent.setState(AgentStates.WAITING);
                    }
                    return;
                }
                break;

            case ATTENDING:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    agent.popNextObjective();
                    this.updateAgentCurrentObjective(agent);
                }
                break;
            case STARTING:
                this.updateAgentCurrentObjective(agent);
                break;

            case WAITING:
                if (agent.getCurrentObjective().canAttend(agent)) {
                    agent.setStartedAttendingAt(currentTime);
                    this.updateAgentCurrentObjective(agent);
                } else if(agent.getPosition().distance(agent.getCurrentObjective().getPosition(agent)) > Constants.MINIMUM_DISTANCE_TO_TARGET) {
                    // update in queue, has to move
                    this.updateAgentCurrentObjective(agent);
                }
                break;

            case LEAVING:
                // does nothing as agent will be removed in next iteration
            default:
        }
    }
}