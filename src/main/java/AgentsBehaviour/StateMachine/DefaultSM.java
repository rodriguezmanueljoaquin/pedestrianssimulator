package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentStates;
import Environment.Objectives.ObjectiveType;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;

public class DefaultSM implements StateMachine {
    protected final Graph graph;

    public DefaultSM(Graph graph) {
        this.graph = graph;
    }

    public void updateAgentCurrentPath(Agent agent) {
        NodePath path = this.graph.getPathToPosition(agent.getPosition(), agent.getCurrentObjective().getPosition(agent));

        if (path == null) {
            // FIXME! Checkear por que a veces da null
            System.out.println("ERROR: NULL searching for path to objective, from: " + agent.getPosition() + " to: " + agent.getCurrentObjective().getPosition(agent));
            agent.setState(AgentStates.LEAVING);
        } else {
            agent.setCurrentPath(path);
        }
    }

    private void updateAgentCurrentObjective(Agent agent, Double currentTime) {
        if (agent.hasObjectives()) {
            this.updateAgentCurrentPath(agent);

            if (agent.getCurrentObjective().getType().equals(ObjectiveType.QUEUE)) {
                agent.setState(AgentStates.MOVING_TO_QUEUE_POSITION);
            } else if (agent.getCurrentObjective().getType().equals(ObjectiveType.STATIC_SERVER) &&
                    agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                //static server has finished attending before agent arrived
                removeFromQueueAndUpdate(agent, currentTime);
            } else {
                agent.setState(AgentStates.MOVING);
            }
        } else {
            agent.setStartedAttendingAt(currentTime);
            agent.setState(AgentStates.LEAVING);
        }
    }

    private void removeFromQueueAndUpdate(Agent agent, Double currentTime) {
        agent.popNextObjective(); //remove queue objective
        this.updateAgentCurrentObjective(agent, currentTime);
    }

    /* BEHAVIOUR OF AN AVERAGE AGENT:
        For each objective:
            if(objective has queue)
                moves to it in a state where target position may vary as it is a queue
                when in the designated position of queue, waits
                if(queue updated)
                    agent goes back to moving now to its new designated position
                else if(server started attending to it)
                    agent starts moving to the designated position server gave to it
            else
                moves to it normally

            when objective reached:
                starts attending to it, once finalized gets next objective
     */

    @Override
    public void movingBehaviour(Agent agent, double currentTime) {
        if (agent.reachedObjective()) {
            // when agent has arrived to objective
            agent.setStartedAttendingAt(currentTime);
            agent.setState(AgentStates.ATTENDING);
        }
    }

    @Override
    public void approximatingBehaviour(Agent agent, double currentTime) {
    }

    @Override
    public void updateAgent(Agent agent, double currentTime) {
        switch (agent.getState()) {
            case MOVING:
                movingBehaviour(agent, currentTime);
                break;

            case ATTENDING:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    agent.setStartedAttendingAt(null); // reset
                    agent.popNextObjective();
                    this.updateAgentCurrentObjective(agent, currentTime);
                }
                break;

            case MOVING_TO_QUEUE_POSITION:
                if (!graph.isPositionVisible(agent.getPosition(), agent.getCurrentObjective().getPosition(agent)))
                    // objective position in queue changed while going to it
                    this.updateAgentCurrentPath(agent);

                if (agent.getCurrentObjective().canAttend(agent))
                    agent.setState(AgentStates.WAITING_IN_QUEUE);
                break;

            case WAITING_IN_QUEUE:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    this.removeFromQueueAndUpdate(agent, currentTime);
                } else if (!agent.reachedObjective()) {
                    // update in queue, has to move
                    this.updateAgentCurrentPath(agent);
                    agent.setState(AgentStates.MOVING_TO_QUEUE_POSITION);
                }
                break;

            case STARTING:
                this.updateAgentCurrentObjective(agent, currentTime);
                break;

            case APPROXIMATING:
                approximatingBehaviour(agent, currentTime);
                break;

            case LEAVING:
                // does nothing as agent will be removed in next iteration
            default:
        }
    }
}