import AgentsGenerator.AgentsGenerator;
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
        List<Wall> walls = InputHandler.importWallsFromTxt("./input/PAREDES.csv");

        // FIXME: CERRAMOS EL CONTORNO PERO DEBERIA SER UN REQUISITO DEL DXF
        walls.add(new Wall(new Vector(0., 0.), new Vector(50, 0)));
        walls.add(new Wall(new Vector(50., 0.), new Vector(50, 20)));
        walls.add(new Wall(new Vector(50., 20.), new Vector(0, 20)));
        walls.add(new Wall(new Vector(0., 20.), new Vector(0, 0)));

        // BORRAR (TEST PARED DIAGONAL)
        walls.add(new Wall(new Vector(15, 0), new Vector(20, 5)));


        Graph graph = new Graph(walls);
        graph.generateGraph(new Vector(1, 1));

        // TESTING:
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getNodes().get(new Vector(1,1)), new Vector(13,20));
//        System.out.println(path);

        // crear servers
        List<Server> servers = new ArrayList<>(); // TODO

        // crear targets
        // TODO: DEBERIA ESTAR EN INPUT Y VENIR DEL DXF
//        List<Target> targets = InputHandler.importTargetFromTxt("./src/Utils/InputExamples/MarketTargets.txt");
        List<Target> targets = InputHandler.importTargetFromTxt("./src/Utils/InputExamples/Plano2Targets.txt");

        // crear generators
        List<AgentsGenerator> generators = InputHandler.importAgentsGeneratorsFromTxt("./input/PEATONES.csv", targets);

        // crear environment
        Environment environment = new Environment(walls, servers, targets, generators);

        try {
            Simulation.createStaticFile(RESULTS_PATH, environment);
            Simulation sim = new Simulation(graph, 200, 0.025, 0.25, environment, RESULTS_PATH);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
