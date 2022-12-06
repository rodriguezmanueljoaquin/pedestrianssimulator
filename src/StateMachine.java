import Agent.Agent;
import GraphGenerator.Graph;
import Agent.AgentStates;
import Utils.Constants;

public class StateMachine {
    public final Graph graph = new Graph(null);

    private StateMachine() {}

    public static void updateAgent(Graph graph, Agent agent, double currentTime) {
        switch (agent.getState()) {
            case MOVING:
                if (agent.getPosition().distance(agent.getCurrentObjective().getPosition()) < Constants.MINIMUM_DISTANCE_TO_TAGRET) {
                    if (agent.getCurrentObjective().hasAttendingTime()) {
                        agent.setStartedAttendingAt(currentTime);
                        agent.setState(AgentStates.ATTENDING);
                        return;
                    } else {
                        agent.setState(AgentStates.WAITING);
                        return;
                    }
                }
                else agent.setState(AgentStates.MOVING);
                break;
            case ATTENDING:
                if (currentTime - agent.getStartedAttendingAt() < agent.getCurrentObjective().getAttendingTime()) {
                    agent.setState(AgentStates.ATTENDING);
                    return;
                }
            case STARTING:
                agent.getNextObjective();
                if (agent.hasObjectives()) {
                    System.out.println("Agent: " + agent.getId() + " at state: " + agent.getState());
                    agent.setCurrentPath(graph.getPathToObjective(agent));
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