package CellIndexMethod;

import Agent.Agent;
import AgentsBehaviour.StateMachine.StateMachine;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import Environment.Wall;
import GraphGenerator.Graph;
import Utils.InputHandler;
import Utils.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CIMTest {
    public static void main(String[] args) {
        List<Wall> walls = new ArrayList<>();
        walls.add(new Wall(new Vector(0., 0.), new Vector(50, 0)));
        walls.add(new Wall(new Vector(50., 0.), new Vector(50, 20)));
        walls.add(new Wall(new Vector(50., 20.), new Vector(0, 20)));
        walls.add(new Wall(new Vector(0., 20.), new Vector(0, 0)));
        StateMachine stateMachine = new SuperMarketClientSM(new Graph(walls));

        List<Agent> agents = new ArrayList<>();
        Agent analyzedAgent = new Agent(new Vector(10,10), 0.1, stateMachine, new ArrayList<>());

        agents.add(analyzedAgent);
        agents.add(new Agent(new Vector(5,5), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(10,5), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(10,15), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(5,10), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(15,10), 0.1, stateMachine, new ArrayList<>()));
        agents.add(new Agent(new Vector(15,15), 0.1, stateMachine, new ArrayList<>()));
        CellIndexMethod cim = new CellIndexMethod(walls, 50);
        cim.updateAgentsPosition(agents);

        for(Agent agent : agents) {
            List<Agent> neighbours = cim.getAgentNeighbours(agent);
            System.out.println(neighbours.stream().map(Agent::getId).sorted().collect(Collectors.toList()));

            List<Agent> neighboursBruteForce = cim.getAgentNeighbourseBruteForce(agent, agents);
            System.out.println(neighboursBruteForce.stream().map(Agent::getId).sorted().collect(Collectors.toList()));
            if(!neighbours.containsAll(neighboursBruteForce))
                throw new RuntimeException();
        }
    }
}
