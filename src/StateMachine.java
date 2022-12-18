import Agent.Agent;
import Agent.AgentStates;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;
import Utils.Constants;

public class StateMachine {
    private StateMachine() {
    }

    private static void updateAgentCurrentObjective(Graph graph, Agent agent) {
        if (agent.hasObjectives()) {
            NodePath path = graph.getPathToPosition(agent.getPosition(), agent.getCurrentObjective().getPosition());

            if (path == null) {
                // FIXME! Checkear por que a veces da null
                System.out.println("ERROR: NULL searching for path to objective, from: " + agent.getPosition() + " to: " + agent.getCurrentObjective().getPosition());
                agent.setState(AgentStates.LEAVING);
            } else {
                agent.setCurrentPath(path);
                agent.setState(AgentStates.MOVING);
            }

        } else {
            agent.setState(AgentStates.LEAVING);
        }
    }

    public static void updateAgent(Graph graph, Agent agent, double currentTime) {
        switch (agent.getState()) {
            case MOVING:
                if (agent.getPosition().distance(agent.getCurrentObjective().getPosition()) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
                    //FixMe: Esto hay que diseñarlo mejor pq sino no podemmos meter el leaving aca tmbn
                    if (agent.getCurrentObjective().hasAttendingTime()) {
                        agent.setStartedAttendingAt(currentTime);
                        agent.setState(AgentStates.ATTENDING);
                    } else {
                        //aca tenemos un tema pq puede ser un server o puede ser leaving, como todavia no hay server
                        //lo dejo en LEAVING
                        agent.setState(AgentStates.LEAVING);
                    }
                    return;
                }
                break;

            case ATTENDING:
                if (currentTime - agent.getStartedAttendingAt() > agent.getCurrentObjective().getAttendingTime()) {
                    agent.popNextObjective();
                    StateMachine.updateAgentCurrentObjective(graph, agent);
                }
                break;

            case STARTING:
                StateMachine.updateAgentCurrentObjective(graph, agent);
                break;

            case WAITING:
                break;

            case LEAVING:
                // does nothing as agent will be removed in next iteration
            default:
        }
    }
}