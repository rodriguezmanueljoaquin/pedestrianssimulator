import Agent.BehaviourScheme;
import AgentsGenerator.AgentsGenerator;
import Environment.*;
import Environment.Server.Server;
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

        // -------- WALLS --------
        List<Wall> walls = InputHandler.importWallsFromTxt("./input/PAREDES.csv");

        // FIXME: CERRAMOS EL CONTORNO PERO DEBERIA SER UN REQUISITO DEL DXF
        walls.add(new Wall(new Vector(0., 0.), new Vector(50, 0)));
        walls.add(new Wall(new Vector(50., 0.), new Vector(50, 20)));
        walls.add(new Wall(new Vector(50., 20.), new Vector(0, 20)));
        walls.add(new Wall(new Vector(0., 20.), new Vector(0, 0)));

        // BORRAR (TEST PARED DIAGONAL)
        walls.add(new Wall(new Vector(15, 0), new Vector(20, 5)));

        List<Exit> exits = InputHandler.importExitsFromTxt("./input/SALIDAS.csv");

        // -------- SERVERS --------
        List<Server> servers = new ArrayList<>(); // TODO

        // -------- TARGETS --------
        // TODO: DEBERIA ESTAR EN INPUT Y VENIR DEL DXF
        // TODO: Y DEBERIA SER UN MAP<Integer, List<Objective>> PARA DIFERENCIAR LOS DISTINTOS GRUPOS DE TARGETS
//        List<Target> targets = InputHandler.importTargetFromTxt("./src/Utils/InputExamples/MarketTargets.txt");
        List<Objective> targets = InputHandler.importTargetsFromTxt("./src/Utils/InputExamples/Plano2Targets.txt");

        // -------- BEHAVIOUR --------
        BehaviourScheme marketClientBehaviourScheme = new BehaviourScheme(exits);

        // SEARCH FOR PRODUCTS
        marketClientBehaviourScheme.addObjectiveGroupToScheme(targets, 5, 10);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> generators = InputHandler.importAgentsGeneratorsFromTxt("./input/PEATONES.csv", marketClientBehaviourScheme);

        // -------- ENVIRONMENT --------
        Environment environment = new Environment(walls, servers, generators, exits);

        // -------- GRAPH --------
        Graph graph = new Graph(environment.getWalls());
        graph.generateGraph(new Vector(1, 1));

        // FOR GRAPH NODES PLOT:
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getNodes().get(new Vector(1,3)), new Vector(25,1));
//        System.out.println(path);

        try {
            Simulation.createStaticFile(RESULTS_PATH, environment);
            Simulation sim = new Simulation(graph, 200, 0.025, 0.25, environment, RESULTS_PATH);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
