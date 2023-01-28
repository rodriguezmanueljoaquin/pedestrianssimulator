import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.StateMachine.StateMachine;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import AgentsGenerator.AgentsGenerator;
import Environment.Environment;
import Environment.Objectives.Exit;
import Environment.Objectives.Objective;
import Environment.Objectives.Server.DynamicServer;
import Environment.Objectives.Server.QueueLine;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Server.StaticServer;
import Environment.Wall;
import GraphGenerator.Graph;
import OperationalModelModule.CPM;
import OperationalModelModule.OperationalModelModule;
import SimulationParameters.SimulationParameters;
import Utils.Constants;
import Utils.InputHandler;
import Utils.Rectangle;
import Utils.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
    private static final String RESULTS_PATH = "./results/";
    public static void main(String[] args) {
        Random random = new Random(1);
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

        // -------- EXITS --------
        List<Exit> exits = InputHandler.importExitsFromTxt("./input/SALIDAS.csv");

        // -------- GRAPH --------
        Graph graph = new Graph(walls, new Vector(1, 1));

        // FOR GRAPH NODES PLOT:
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getNodes().get(new Vector(1,3)), new Vector(25,1));
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParameters parameters = new SimulationParameters("./input/parameters.json");

        // -------- SERVERS --------
        List<Server> servers = new ArrayList<>();

        servers.add(new StaticServer(2,
                new Rectangle(new Vector(45, 10), new Vector(50, 20)),
                20, 100
        )); // supongamos que esto es clase de MATEMATICAS
        servers.add(new StaticServer(3,
                new Rectangle(new Vector(36, 10), new Vector(40, 20)),
                80, 100
        )); // supongamos que esto es clase de LENGUA
        servers.add(new DynamicServer(2,
                new Rectangle(new Vector(1, 10), new Vector(15, 20)),
                10,
                new QueueLine(new Vector(5, 4), new Vector(20, 4)))
        ); // supongamos que esto es una zona con maquinas expendedoras


        // -------- TARGETS --------
        // TODO: Y DEBERIA SER UN MAP<Integer, List<Objective>> PARA DIFERENCIAR LOS DISTINTOS GRUPOS DE TARGETS
        Map<String, List<Objective>> targets = InputHandler.importTargetsFromTxt("./input/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- BEHAVIOUR --------
        // ---- STATE MACHINE ----
        StateMachine stateMachine = new SuperMarketClientSM(graph);

        BehaviourScheme studentBehaviourScheme = new BehaviourScheme(stateMachine, exits, graph, random.nextLong());

        // ---- ADD OBJECTIVES GROUP ----
        List<Objective> serverObjectives = new ArrayList<>();
        serverObjectives.addAll(servers);
        studentBehaviourScheme.addObjectiveGroupToScheme(serverObjectives, 1, 3);

        studentBehaviourScheme.addObjectiveGroupToScheme(targets.get("PRODUCT"), 2, 5);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> studentsGenerators =
                InputHandler.importAgentsGeneratorsFromTxt("./input/PEATONES.csv", studentBehaviourScheme, random.nextLong());

        // -------- ENVIRONMENT --------
        Environment environment = new Environment(walls, servers, studentsGenerators, exits);

        // -------- OPERATIONAL MODEL MODULE --------
        OperationalModelModule operationalModelModule = new CPM(environment);

        try {
            Simulation.createStaticFile(RESULTS_PATH, environment);
            Simulation sim = new Simulation(Constants.MAX_TIME, environment, operationalModelModule, RESULTS_PATH, random);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
