import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import AgentsGenerator.AgentsGenerator;
import Environment.Environment;
import Environment.Objectives.Exit;
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

//        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCTS")), 2, 3);
//        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("LETTERS")), 1, 1);
        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(serversMap.get("CASHIER")), 1, 1);

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
        List<Wall> walls = CSVHandler.importWalls("./input/WALLS.csv");

        // -------- EXITS --------
        List<Exit> exits = CSVHandler.importExits("./input/EXITS.csv");

        // -------- GRAPH --------
        Graph graph = new Graph(walls, exits.stream().map(Exit::getExitWall).collect(Collectors.toList()), new Vector(1, 1));

        // FOR GRAPH NODES PLOT:
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getClosestVisibleNode(new Vector(16.07,9.65)), new Vector(34.5, 12.0));
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParameters parameters = new SimulationParameters("./input/parameters.json");

        // -------- TARGETS --------
        Map<String, List<Target>> targetsMap = CSVHandler.importTargets("./input/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- SERVERS --------
        Map<String, List<Server>> serversMap = CSVHandler.importServers("./input/SERVERS_FIXED.csv", parameters.getServerGroupsParameters());

        // -------- BEHAVIOUR --------
        Map<String, BehaviourScheme> behaviours = getBehaviourSchemes(graph, exits, serversMap, targetsMap, random);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> generators = CSVHandler.importAgentsGenerators(
                "./input/GENERATORS.csv", behaviours,
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
