import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import AgentsGenerator.AgentsGenerator;
import Environment.Environment;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Target.Target;
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

    private static Map<String, BehaviourScheme> getBehaviourSchemes(Graph graph, Map<String, List<Exit>> exitsMap,
                                                                    Map<String, List<Server>> serversMap, Map<String, List<Target>> targetsMap,
                                                                    Random random) {
        Map<String, BehaviourScheme> behaviourSchemes = new HashMap<>();

        // --- MARKET-CLIENT ---
        BehaviourScheme marketClientBehaviourScheme = new BehaviourScheme(new SuperMarketClientSM(graph), exitsMap.get("NORMAL"),
                graph, random);

        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCT1")), 2, 3);
        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCT2")), 1, 1);
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
        Map<String, List<Exit>> exitsMap = CSVHandler.importExits("./input/EXITS.csv");

        // -------- GRAPH --------
        Graph graph = new Graph(walls, exitsMap.values().stream().flatMap(List::stream)
                .map(Exit::getExitWall).collect(Collectors.toList()), new Vector(1, 1));

        // FOR GRAPH NODES PLOT  -------- DEBUGGING --------
//        graph.generateOutput(RESULTS_PATH);
//        NodePath path = graph.AStar(graph.getClosestAccessibleNode(new Vector(6.542717913594863,-.5), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS),
//                new Vector(25., 5.0), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS);
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParameters parameters = new SimulationParameters("./input/parameters.json", random);

        // -------- TARGETS --------
        Map<String, List<Target>> targetsMap = CSVHandler.importTargets("./input/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- SERVERS --------
        Map<String, List<Server>> serversMap = CSVHandler.importServers("./input/SERVERS.csv", parameters.getServerGroupsParameters());

        // -------- BEHAVIOUR --------
        Map<String, BehaviourScheme> behaviours = getBehaviourSchemes(graph, exitsMap, serversMap, targetsMap, random);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> generators = CSVHandler.importAgentsGenerators(
                "./input/GENERATORS.csv", behaviours,
                parameters.getGeneratorsParameters(), random.nextLong()
        );

        // -------- ENVIRONMENT --------
        List<Server> servers = serversMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Environment environment = new Environment(walls, servers, generators,
                exitsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()) // all exits
        );

        // -------- OPERATIONAL MODEL MODULE --------
        OperationalModelModule operationalModelModule = new CPM(environment);

        try {
            Simulation.createStaticFile(RESULTS_PATH, walls);
            Simulation sim = new Simulation(parameters.getMaxTime(), environment, operationalModelModule, RESULTS_PATH, random, (double) parameters.getEvacuationTime());
            sim.run();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
