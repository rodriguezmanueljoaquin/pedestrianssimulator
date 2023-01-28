package CellIndexMethod;

import Agent.Agent;
import AgentsBehaviour.StateMachine.StateMachine;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import Environment.Wall;
import GraphGenerator.Graph;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CIMTest {

    private static List<Agent> getAgentNeighboursBruteForce(Agent agent, List<Agent> agents, double neighbourhoodRadius) {
        List<Agent> agentList = new ArrayList<>();
        for (Agent neighbour : agents) {
            if (!Objects.equals(agent.getId(), neighbour.getId()) && agent.distance(neighbour) <= neighbourhoodRadius)
                agentList.add(neighbour);
        }
        return agentList;
    }

    public static void main(String[] args) {
        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(new Vector(0., 0.), new Vector(50, 0)));
        walls.add(new Wall(new Vector(50., 0.), new Vector(50, 20)));
        walls.add(new Wall(new Vector(50., 20.), new Vector(0, 20)));
        walls.add(new Wall(new Vector(0., 20.), new Vector(0, 0)));
        StateMachine stateMachine = new SuperMarketClientSM(new Graph(walls, new Vector(1,1)));

        List<Agent> agents = new ArrayList<>();
        Agent analyzedAgent = new Agent(new Vector(10, 10), 0.1, stateMachine, new ArrayList<>());
        double neighbourhoodRadius = 9;

        agents.add(analyzedAgent);
        agents.add(new Agent(new Vector(5, 5), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(10, 5), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(10, 15), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(5, 10), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(15, 10), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(15, 15), 0.1, stateMachine, new ArrayList<>()));
        CellIndexMethod cim = new CellIndexMethod(walls, neighbourhoodRadius);
        cim.updateAgentsPosition(agents);

        for (Agent agent : agents) {
            List<Agent> neighboursCIM = cim.getAgentNeighbours(agent);
            List<Agent> neighboursBruteForce = getAgentNeighboursBruteForce(agent, agents, neighbourhoodRadius);

            if (!neighboursCIM.containsAll(neighboursBruteForce))
                throw new RuntimeException();
        }
    }
}
