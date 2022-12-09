import Agent.Agent;
import Environment.Environment;
import Environment.Server;
import Environment.Target;
import Environment.Wall;
import GraphGenerator.Graph;
import Utils.InputHandler;
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
//        List<Wall> walls = InputHandler.importWallsFromTxt("./src/Utils/InputExamples/SegmentosGeometria.txt");
        List<Wall> walls = InputHandler.importWallsFromTxt("./input/PAREDES.csv");

        // FIXME: CERRAMOS EL CONTORNO PERO DEBERIA SER UN REQUISITO DEL DXF
        walls.add(new Wall(new Vector(0., 0.), new Vector(100, 0)));
        walls.add(new Wall(new Vector(0., 100.), new Vector(100, 100)));
        walls.add(new Wall(new Vector(0., 0.), new Vector(0, 100)));
        walls.add(new Wall(new Vector(100., 0.), new Vector(100, 100)));

        // TEST PARED DIAGONAL:
        walls.add(new Wall(new Vector(10., 10.), new Vector(50, 50)));

        walls.add(new Wall(new Vector(2., 20.), new Vector(50, 20)));

        Graph graph = new Graph(walls);
        graph.generateGraph(new Vector(1, 1));

        // TESTING:
        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getNodes().get(new Vector(1,1)), new Vector(13,20));
//        System.out.println(path);

        // crear servers
        List<Server> servers = new ArrayList<>();

        // crear targets
        List<Target> targets = InputHandler.importTargetFromTxt("./src/Utils/InputExamples/ProductList.txt");

        // crear agents TODO: GENERATORS
        List<Agent> agents = new ArrayList<>();
//        agents.add(new Agent(new Vector(1, 2), new Vector(0, 0), 0.5, new ArrayList<>(targets.subList(0,10))));
//        agents.add(new Agent(new Vector(4,6), new Vector(0,0), 0.5, new ArrayList<>(targets.subList(10,20))));
//        agents.add(new Agent(new Vector(1,31), new Vector(0,0), 0.5, new ArrayList<>(targets.subList(5,15))));
//        agents.add(new Agent(new Vector(10,26), new Vector(0,0), 0.5, new ArrayList<>(targets.subList(15,25))));
//        agents.add(new Agent(new Vector(8,18), new Vector(0,0), 0.5, new ArrayList<>(targets.subList(1,11))));

        // crear environment
        Environment environment = new Environment(walls, servers, targets, 10., 10.);

        try {
            Simulation.createStaticFile(RESULTS_PATH, environment);
            Simulation sim = new Simulation(agents, graph, 1000, 0.025, 0.25, environment, RESULTS_PATH);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}