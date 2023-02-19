package Environment;

import Agent.Agent;
import AgentsGenerator.AgentsGenerator;
import Environment.Objectives.Server.Server;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private final List<Wall> walls;
    private final List<Server> servers;
    private final List<AgentsGenerator> generators;

    public Environment(List<Wall> walls, List<Server> servers, List<AgentsGenerator> generators) {
        this.walls = walls;
        this.servers = servers;
        this.generators = generators;
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

    public List<Agent> generateAgents(Double time) {
        List<Agent> newAgents = new ArrayList<>();

        for (AgentsGenerator generator : this.generators) {
            newAgents.addAll(generator.generate(time));
        }
        return newAgents;
    }

    public List<Agent> update(Double time) {
        for (Server server : this.getServers()) {
            server.updateServer(time);
        }

        return generateAgents(time);
    }

}
