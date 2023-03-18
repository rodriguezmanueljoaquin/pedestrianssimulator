package Environment;

import Agent.Agent;
import AgentsGenerator.AgentsGenerator;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.Server;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private final List<Wall> walls;
    private final List<Server> servers;
    private final List<AgentsGenerator> generators;
    private final List<Exit> exits;

    public Environment(List<Wall> walls, List<Server> servers, List<AgentsGenerator> generators, List<Exit> exits) {
        this.walls = walls;
        this.servers = servers;
        this.generators = generators;
        this.exits = exits;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Server> getServers() {
        return servers;
    }

    public Wall getClosestWall(Vector position) {
        Wall closestWall = walls.get(0);
        Double minDistance = closestWall.getClosestPoint(position).distance(position);
        Double currentDistance;
        for (Wall wall : walls) {
            currentDistance = wall.getClosestPoint(position).distance(position);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestWall = wall;
            }
        }
        return closestWall;
    }

    public List<Exit> getExits() {
        return exits;
    }

    public List<Agent> generateAgents(Double time, List<Agent> currentAgents) {
        List<Agent> newAgents = new ArrayList<>();

        for (AgentsGenerator generator : this.generators) {
            newAgents.addAll(generator.generate(time, currentAgents));
        }
        return newAgents;
    }

    public List<Agent> update(Double time, List<Agent> currentAgents) {
        for (Server server : this.getServers()) {
            server.updateServer(time);
        }

        return generateAgents(time, currentAgents);
    }

}
