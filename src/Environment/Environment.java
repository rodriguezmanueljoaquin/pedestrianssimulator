package Environment;

import java.util.List;

public class Environment {
    private List<Wall> walls;
    private List<Server> servers;
    private List<Target> targets;
    private double width, height;

    public Environment(List<Wall> walls, List<Server> servers, List<Target> targets, double width, double height) {
        this.walls = walls;
        this.servers = servers;
        this.targets = targets;
        this.width = width;
        this.height = height;
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
