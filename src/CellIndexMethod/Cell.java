package CellIndexMethod;

import Agent.Agent;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final List<Agent> agents = new ArrayList<>();

    public Cell(List<Agent> agents) {
        this.agents.addAll(agents);
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void addAgents(List<Agent> agents){
        this.agents.addAll(agents);
    }

    public void clear(){
        this.agents.clear();
    }
}