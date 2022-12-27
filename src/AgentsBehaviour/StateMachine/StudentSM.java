package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentStates;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;

public class StudentSM implements StateMachine {
    private final Graph graph;

    public StudentSM(Graph graph) {
        this.graph = graph;
    }

    private void updateAgentCurrentPath(Agent agent) {
        NodePath path = this.graph.getPathToPosition(agent.getPosition(), agent.getCurrentObjective().getPosition(agent));

        if (path == null) {
            // FIXME! Checkear por que a veces da null
            System.out.println("ERROR: NULL searching for path to objective, from: " + agent.getPosition() + " to: " + agent.getCurrentObjective().getPosition(agent));
            agent.setState(AgentStates.LEAVING);
        } else {
            agent.setCurrentPath(path);
        }
    }

    private void updateAgentCurrentObjective(Agent agent) {
        if (agent.hasObjectives()) {
            this.updateAgentCurrentPath(agent);

            if (agent.getCurrentObjective().isQueue())
                agent.setState(AgentStates.MOVING_TO_QUEUE_POSITION);
            else {
                agent.setState(AgentStates.MOVING);
            }
        } else {
            agent.setState(AgentStates.LEAVING);
        }
    }

    private void removeFromQueueAndUpdate(Agent agent) {
        agent.popNextObjective(); //remove queue objective
        this.updateAgentCurrentObjective(agent);
    }

    @Override
    public void updateAgent(Agent agent, double currentTime) {
        switch (agent.getState()) {
            case MOVING:
                // TODO: TRY WITH STATIC
//                if(agent.getCurrentObjective().isServer() && agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
//                    agent.setState(AgentStates.ATTENDING);
//                    return;
//                }
                if (agent.reachedObjective()) {
                    if (!agent.getCurrentObjective().canAttend(agent)) {
                        throw new RuntimeException("Objective that should be attendable says it is not");
                    }
                    agent.setStartedAttendingAt(currentTime);
                    agent.setState(AgentStates.ATTENDING);
                    return;
                }
                break;

            case ATTENDING:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    agent.setStartedAttendingAt(null); // reset
                    agent.popNextObjective();
                    this.updateAgentCurrentObjective(agent);
                }
                break;

            case MOVING_TO_QUEUE_POSITION:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    this.removeFromQueueAndUpdate(agent); // start attending to server without doing the queue

                } else if (agent.getCurrentObjective().canAttend(agent))
                    agent.setState(AgentStates.WAITING_IN_QUEUE);
                break;

            case WAITING_IN_QUEUE:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    this.removeFromQueueAndUpdate(agent);
                } else if (!agent.reachedObjective()) {
                    // update in queue, has to move
                    this.updateAgentCurrentPath(agent);
                    agent.setState(AgentStates.MOVING_TO_QUEUE_POSITION);
                }
                break;

            case STARTING:
                this.updateAgentCurrentObjective(agent);
                break;

            case LEAVING:
                // does nothing as agent will be removed in next iteration
            default:
        }
    }
}