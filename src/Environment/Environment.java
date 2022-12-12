package Environment;

import Agent.Agent;
import Agent.AgentsGenerator;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private List<Wall> walls;
    private List<Server> servers;
    private List<Target> targets;
    private List<AgentsGenerator> generators;

    public Environment(List<Wall> walls, List<Server> servers, List<Target> targets, List<AgentsGenerator> generators) {
        this.walls = walls;
        this.servers = servers;
        this.targets = targets;
        this.generators = generators;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public List<Agent> generateAgents(Double time) {
        List<Agent> newAgents = new ArrayList<>();

        for (AgentsGenerator generator: this.generators) {
            newAgents.addAll(generator.generate(time));
        }

        return newAgents;
    }

}
