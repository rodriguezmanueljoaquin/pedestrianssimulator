package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentStates;
import Environment.Objectives.Exit;
import Environment.Objectives.ObjectiveType;
import GraphGenerator.Graph;
import GraphGenerator.Node;
import GraphGenerator.NodePath;
import Utils.Constants;
import Utils.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static Utils.Constants.LEAVING_TIME;

public class DefaultSM implements StateMachine {
    protected final Graph graph;

    public DefaultSM(Graph graph) {
        this.graph = graph;
    }

    private void updateAgentCurrentPath(Agent agent) {
        NodePath path = this.graph.getPathToPosition(agent.getPosition(), agent.getCurrentObjective().getPosition(agent), agent.getMaxRadius());

        if (path == null) {
            System.out.println("ERROR: NULL searching for path to objective, from: " + agent.getPosition() + " to: " + agent.getCurrentObjective().getPosition(agent));
            agent.setState(AgentStates.LEAVING);
        } else {
            agent.setCurrentPath(path);
        }
    }

    public void evacuate(Agent agent, List<Exit> exits) {
        agent.setState(AgentStates.MOVING);

        Vector closestExitPosition = this.graph.getClosestDestination(agent.getPosition(),
                exits.stream().map(Exit::getCentroidPosition).collect(Collectors.toList()), agent.getMaxRadius());

        // get the exit with matches with the closest position found
        Exit closestExit = exits.stream()
                .filter(e -> e.getCentroidPosition().distance(closestExitPosition) < Constants.DOUBLE_EPSILON).findFirst()
                .orElseThrow(RuntimeException::new);

        agent.setObjectives(new ArrayList<>(Collections.singletonList(closestExit)));
        this.updateAgentCurrentPath(agent);
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
        if (agent.getCurrentObjective().reachedObjective(agent)) {
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
        if (currentTime % 10 < 1 && (agent.getState() == AgentStates.MOVING || agent.getState() == AgentStates.MOVING_TO_QUEUE_POSITION)) {
            Node intermediateObjectiveNode = agent.getIntermediateObjectiveNode();
            // every 10 frames
            // check if agent current path is correct (maybe agent got wrongly redirected because of CPM heuristics), otherwise update it
            if (intermediateObjectiveNode == null) {
                if (!this.graph.isPositionAccessible(agent.getPosition(), agent.getCurrentObjective().getPosition(agent), agent.getMaxRadius())) {
                    this.updateAgentCurrentPath(agent);
                }
            } else {
                if (!this.graph.isPositionAccessible(agent.getPosition(), intermediateObjectiveNode.getPosition(), agent.getMaxRadius())) {
                    this.updateAgentCurrentPath(agent);
                }
            }
        }

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
                Node lastNode = agent.getCurrentPath().getLastNode();
                if (lastNode != null && !this.graph.isPositionAccessible(lastNode.getPosition(),
                        agent.getCurrentObjective().getPosition(agent),
                        agent.getMaxRadius()))
                    // objective position in queue changed while going to it and the position is not accessible from current path
                    this.updateAgentCurrentPath(agent);

                if (agent.getCurrentObjective().canAttend(agent))
                    agent.setState(AgentStates.WAITING_IN_QUEUE);
                break;

            case WAITING_IN_QUEUE:
                if (agent.getCurrentObjective().hasFinishedAttending(agent, currentTime)) {
                    this.removeFromQueueAndUpdate(agent, currentTime);
                } else if (!agent.getCurrentObjective().reachedObjective(agent)) {
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
                if (currentTime - agent.getStartedAttendingAt() > LEAVING_TIME) {
                    agent.setState(AgentStates.LEFT);
                }
            default:
        }
    }
}