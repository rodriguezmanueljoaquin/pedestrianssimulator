package Environment;

import Agent.Agent;
import AgentsGenerator.AgentsGenerator;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private List<Wall> walls;
    private List<Server> servers;
    private List<AgentsGenerator> generators;
    private List<Exit> exits;

    public Environment(List<Wall> walls, List<Server> servers, List<AgentsGenerator> generators, List<Exit> exits) {
        this.walls = walls;
        this.servers = servers;
        this.generators = generators;
        this.exits = exits;
    }

    public List<Exit> getExits() {
        return exits;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Server> getServers() {
        return servers;
    }

    public Exit getNearestExit(Vector position) {
        //TODO: Update with Graph function getPathLengthToObjective(Vector fromPostion, Vector toPosition)
        //Once we have behaviour module
        Exit minExit = getExits().get(0);
        Double minDistance = position.distance(minExit.getPosition());
        Double currentDistance;
        for (Exit currentExit : getExits()) {
            currentDistance = position.distance(currentExit.getPosition());
            if (currentDistance < minDistance) {
                minExit = currentExit;
                minDistance = currentDistance;
            }
        }
        return minExit;
    }

    public List<Agent> generateAgents(Double time) {
        List<Agent> newAgents = new ArrayList<>();

        for (AgentsGenerator generator : this.generators) {
            newAgents.addAll(generator.generate(time));
        }
        return newAgents;
    }

}
