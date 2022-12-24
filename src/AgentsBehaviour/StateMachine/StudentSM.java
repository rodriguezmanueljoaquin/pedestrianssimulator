package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentStates;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;
import Utils.Constants;

public class StudentSM implements StateMachine{
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
                if (agent.getPosition().distance(agent.getCurrentObjective().getPosition(agent)) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
                    //FixMe: Esto hay que disenarlo mejor pq sino no podemmos meter el leaving aca tmbn
                    if (agent.getCurrentObjective().hasToAttend(agent)) {
                        agent.setStartedAttendingAt(currentTime);
                        agent.setState(AgentStates.ATTENDING);
                    } else {
                        //aca tenemos un tema pq puede ser un server o puede ser leaving, como todavia no hay server
                        //lo dejo en LEAVING
                        if(agent.getCurrentObjective().isServer())
                            agent.setState(AgentStates.MOVING);
                        else agent.setState(AgentStates.LEAVING);
                    }
                    return;
                }
                break;

            case ATTENDING:
                if (agent.getCurrentObjective().hasFinishedAttending(agent,agent.getStartedAttendingAt(),currentTime)) {
                    agent.popNextObjective();
                    this.updateAgentCurrentObjective(agent);
                }
                break;
            case STARTING:
                this.updateAgentCurrentObjective(agent);
                break;

            case WAITING:
                if (agent.getCurrentObjective().hasToAttend(agent)) {
                    agent.setStartedAttendingAt(currentTime);
                    agent.setState(AgentStates.ATTENDING);
                }
                break;

            case LEAVING:
                // does nothing as agent will be removed in next iteration
            default:
        }
    }
}