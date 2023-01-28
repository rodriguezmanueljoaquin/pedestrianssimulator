import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.StateMachine.StateMachine;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import AgentsGenerator.AgentsGenerator;
import Environment.Environment;
import Environment.Objectives.Exit;
import Environment.Objectives.Objective;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Target;
import Environment.Wall;
import GraphGenerator.Graph;
import InputHandling.EnvironmentData.CSVHandler;
import InputHandling.SimulationParameters.SimulationParameters;
import OperationalModelModule.CPM;
import OperationalModelModule.OperationalModelModule;
import Utils.Constants;
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
import java.util.stream.Collectors;

public class Main {
    private static final String RESULTS_PATH = "./results/";

    public static void main(String[] args) {
        Random random = new Random(1);
        if (!Files.exists(Paths.get(RESULTS_PATH))) {
            new File(RESULTS_PATH).mkdir();
        }
        // -------- WALLS --------
        List<Wall> walls = CSVHandler.importWalls("./input/PAREDES.csv");

        // FIXME: AGREGAMOS WALLS EN LAS EXITS QUE EN LOS DXF NO ESTABAN
        walls.add(new Wall(new Vector(22.5, 0.), new Vector(27.5, 0.)));
        walls.add(new Wall(new Vector(22.5, 20.), new Vector(27.5, 20.)));

        walls.add(new Wall(new Vector(15, 0), new Vector(20, 5)));
        // BORRAR ESTAS WALLS UNA VEZ Q SE TENGA UN DXF CORRECTO

        // -------- EXITS --------
        List<Exit> exits = CSVHandler.importExits("./input/SALIDAS.csv");

        // -------- GRAPH --------
        Graph graph = new Graph(walls, new Vector(1, 1));

        // FOR GRAPH NODES PLOT:
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getNodes().get(new Vector(1,3)), new Vector(25,1));
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParameters parameters = new SimulationParameters("./input/parameters.json");

        // -------- SERVERS --------
        Map<String, List<Server>> serversMap = CSVHandler.importServers("./input/SERVERS.csv", parameters.getServerGroupsParameters());

        // -------- TARGETS --------
        Map<String, List<Target>> targetsMap = CSVHandler.importTargets("./input/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- BEHAVIOUR --------
        // ---- STATE MACHINE ----
        StateMachine stateMachine = new SuperMarketClientSM(graph);

        BehaviourScheme studentBehaviourScheme = new BehaviourScheme(stateMachine, exits, graph, random.nextLong());

        // ---- ADD OBJECTIVE GROUPS ----
        List<Objective> serverObjectives = new ArrayList<>(serversMap.get("CASHIER"));
        studentBehaviourScheme.addObjectiveGroupToScheme(serverObjectives, 1, 3);

        List<Objective> targetObjectives = new ArrayList<>(targetsMap.get("PRODUCT"));
        studentBehaviourScheme.addObjectiveGroupToScheme(targetObjectives, 2, 5);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> studentsGenerators =
                CSVHandler.importAgentsGenerators("./input/PEATONES.csv", studentBehaviourScheme, random.nextLong());

        // -------- ENVIRONMENT --------
        List<Server> servers = serversMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Environment environment = new Environment(walls, servers, studentsGenerators, exits);

        // -------- OPERATIONAL MODEL MODULE --------
        OperationalModelModule operationalModelModule = new CPM(environment);

        try {
            Simulation.createStaticFile(RESULTS_PATH, walls);
            Simulation sim = new Simulation(Constants.MAX_TIME, environment, operationalModelModule, RESULTS_PATH, random);
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
