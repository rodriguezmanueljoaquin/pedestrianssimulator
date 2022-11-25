import Walls.Wall;

import java.util.List;

public class Environment {
    private List<Wall> walls;
    private List<Server> servers;
    private double length, height;

    public Environment(List<Wall> walls, List<Server> servers, double length, double height) {
        this.walls = walls;
        this.servers = servers;
        this.length = length;
        this.height = height;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Server> getServers() {
        return servers;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }
}
