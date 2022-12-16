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
                } else agent.setState(AgentStates.MOVING);
                break;
            case ATTENDING:
                if (currentTime - agent.getStartedAttendingAt() < agent.getCurrentObjective().getAttendingTime()) {
                    agent.setState(AgentStates.ATTENDING);
                    return;
                }
                agent.getNextObjective();
            case STARTING:
                if (agent.hasObjectives()) {
                    NodePath path = graph.getPathToObjective(agent);
                    if (path == null) {
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
                //PROBLEMA TUVE QUE MATAR AL AGENTE DESDE SIMULATION
                //NO SE PUEDE MATAR DESDE ACA YA QUE ESTA EN UNA LISTA Y VA A DAR NULL POINTER EXCEPTION
            case WAITING:
                break;

        }
    }
}