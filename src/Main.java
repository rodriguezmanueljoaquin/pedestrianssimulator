import Agent.Agent;
import Environment.Environment;
import Environment.Server;
import GraphGenerator.Graph;
import GraphGenerator.NodePath;
import Utils.DFXHandler;
import Environment.Wall;
import Environment.Target;
import Environment.Objective;
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
        List<Wall> walls = DFXHandler.importWallsFromTxt("./src/Utils/DFXExamples/SegmentosGeometria.txt");
        walls.add(new Wall(new Vector(0.,0.), new Vector(14,0)));

        Graph graph = new Graph(walls);
        graph.generateGraph(new Vector(1,1));
        graph.generateOutput(RESULTS_PATH);
        NodePath path = graph.AStar(graph.getNodes().get(new Vector(1,1)), new Vector(13,20));
        System.out.println(path);

        // crear servers
        List<Server> servers = new ArrayList<>();

        // crear targets
        List<Target> targets = DFXHandler.importTargetFromTxt("./src/Utils/DFXExamples/ProductList.txt");

        // crear agents
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(new Vector(1,2), new Vector(0,0), 0.5, targets));
//        agents.add(new Agent(new Vector(4,6), new Vector(0,0), 0.5, targets));

        // crear environment
        Environment environment = new Environment(walls, servers, targets, 10., 10.);

        try {
            Simulation.createStaticFile(RESULTS_PATH, environment);
            Simulation sim = new Simulation(agents, graph,10000, 0.25,0.5, environment, RESULTS_PATH);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}