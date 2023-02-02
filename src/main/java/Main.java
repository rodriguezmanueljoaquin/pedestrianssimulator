import AgentsBehaviour.BehaviourScheme;
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
import InputHandling.ParametersNames;
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
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String RESULTS_PATH = "./results/";

    private static Map<String, BehaviourScheme> getBehaviourSchemes(Graph graph, List<Exit> exits,
                                                                    Map<String, List<Server>> serversMap, Map<String, List<Target>> targetsMap,
                                                                    Random random) {
        Map<String, BehaviourScheme> behaviourSchemes = new HashMap<>();

        // --- MARKET-CLIENT ---
        BehaviourScheme marketClientBehaviourScheme = new BehaviourScheme(new SuperMarketClientSM(graph), exits, graph, random.nextLong());

        List<Objective> targetObjectives = new ArrayList<>(targetsMap.get("PRODUCT"));
        marketClientBehaviourScheme.addObjectiveGroupToScheme(targetObjectives, 2, 5);
        List<Objective> serverObjectives = new ArrayList<>(serversMap.get("CASHIER"));
        marketClientBehaviourScheme.addObjectiveGroupToScheme(serverObjectives, 1, 1);

        behaviourSchemes.put(ParametersNames.MARKET_CLIENT_BEHAVIOUR_SCHEME_KEY, marketClientBehaviourScheme);
        // ---   ---

        return behaviourSchemes;
    }

    public static void main(String[] args) {
        Random random = new Random(1);
        if (!Files.exists(Paths.get(RESULTS_PATH))) {
            new File(RESULTS_PATH).mkdir();
        }
        // -------- WALLS --------
        List<Wall> walls = CSVHandler.importWalls("./input/PAREDES.csv");


        walls.add(new Wall(new Vector(15, 0), new Vector(20, 5)));
        // BORRAR ESTAS WALLS UNA VEZ Q SE TENGA UN DXF CORRECTO

        // -------- EXITS --------
        List<Exit> exits = CSVHandler.importExits("./input/SALIDAS.csv");

        // -------- GRAPH --------
        List<Wall> wallsAndExits = new ArrayList<>();
        wallsAndExits.addAll(walls);
        wallsAndExits.addAll(exits.stream().map(Exit::getExitWall).collect(Collectors.toList()));
        Graph graph = new Graph(wallsAndExits, new Vector(1, 1));

        // FOR GRAPH NODES PLOT:
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getClosestVisibleNode(new Vector(16.07,9.65)), new Vector(34.5, 12.0));
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParameters parameters = new SimulationParameters("./input/parameters.json");

        // -------- SERVERS --------
        Map<String, List<Server>> serversMap = CSVHandler.importServers("./input/SERVERS.csv", parameters.getServerGroupsParameters());

        // -------- TARGETS --------
        Map<String, List<Target>> targetsMap = CSVHandler.importTargets("./input/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- BEHAVIOUR --------
        Map<String, BehaviourScheme> behaviours = getBehaviourSchemes(graph, exits, serversMap, targetsMap, random);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> generators = CSVHandler.importAgentsGenerators(
                "./input/AGENT_GENERATORS.csv", behaviours,
                parameters.getGeneratorsParameters(), random.nextLong()
        );

        // -------- ENVIRONMENT --------
        List<Server> servers = serversMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Environment environment = new Environment(walls, servers, generators, exits);

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
