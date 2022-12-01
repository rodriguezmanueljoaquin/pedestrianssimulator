import Agent.Agent;
import Environment.Environment;
import Environment.Server;
import Utils.DFXHandler;
import Environment.Wall;
import Environment.Target;
import Utils.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String RESULTS_PATH = "results/";
        if (!Files.exists(Paths.get(RESULTS_PATH))) {
            new File(RESULTS_PATH).mkdir();
        }

        // crear walls
        List<Wall> walls = DFXHandler.importWallsFromDFX("./src/Utils/DFXExamples/SegmentosGeometria.txt");

        // crear agents
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(new Vector(2,2), new Vector(0,0), 0.5));
        agents.add(new Agent(new Vector(5,6), new Vector(0,0), 0.5));

        // crear servers
        List<Server> servers = new ArrayList<>();

        // crear targets
        List<Target> targets = new ArrayList<>();

        // crear environment
        Environment environment = new Environment(walls, servers, targets, 10., 10.);

        try {
            Simulation.createStaticFile(RESULTS_PATH, environment);
            Simulation sim = new Simulation(agents, 10, 0.01,0.1, environment, RESULTS_PATH);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}