import Agent.Agent;
import Agent.AgentStates;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;
import Utils.Constants;

public class StateMachine {
    public final Graph graph = new Graph(null);

    private StateMachine() {
    }

    public static void updateAgent(Graph graph, Agent agent, double currentTime) {
        switch (agent.getState()) {
            case MOVING:
                if (agent.getPosition().distance(agent.getCurrentObjective().getPosition()) < Constants.MINIMUM_DISTANCE_TO_TARGET) {
                    if (agent.getCurrentObjective().hasAttendingTime()) {
                        agent.setStartedAttendingAt(currentTime);
                        agent.setState(AgentStates.ATTENDING);
                    } else {
                        agent.setState(AgentStates.WAITING);
                    }
                    return;
                } else agent.setState(AgentStates.MOVING);
                break;
            case ATTENDING:
                if (currentTime - agent.getStartedAttendingAt() < agent.getCurrentObjective().getAttendingTime()) {
                    agent.setState(AgentStates.ATTENDING);
                    return;
                }
            case STARTING:
                agent.getNextObjective();
                if (agent.hasObjectives()) {
                    NodePath path = graph.getPathToObjective(agent);
                    if(path == null) {
                        // FIXME! Checkear por que a veces da null
                        agent.setState(AgentStates.LEAVING);
                        return;
                    }

                    agent.setCurrentPath(path);
                    agent.setState(AgentStates.MOVING);
                    return;
                }
                agent.setState(AgentStates.LEAVING);
                break;
            case LEAVING:
            case WAITING:
                break;

        }
    }
}