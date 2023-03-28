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
import InputHandling.SimulationParameters.SimulationParametersParser;
import OperationalModelModule.CPMAnisotropic;
import OperationalModelModule.OperationalModelModule;
import Utils.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String RESULTS_DIRECTORY = "./out/";
    private static final String CSV_DIRECTORY = "./tmp/simulation_input";

    private static Map<String, BehaviourScheme> getBehaviourSchemes(Graph graph, Map<String, List<Exit>> exitsMap,
                                                                    Map<String, List<Server>> serversMap, Map<String, List<Target>> targetsMap,
                                                                    double agentsMaximumMostPossibleRadius, Random random) {
        Map<String, BehaviourScheme> behaviourSchemes = new HashMap<>();

        // --- MARKET-CLIENT ---
        BehaviourScheme marketClientBehaviourScheme = new BehaviourScheme(new SuperMarketClientSM(graph), exitsMap.get("NORMAL"),
                graph, agentsMaximumMostPossibleRadius, random);

        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCT1")), 2, 3);
        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCT2")), 1, 1);
        marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(serversMap.get("CASHIER")), 1, 1);

        behaviourSchemes.put(ParametersNames.MARKET_CLIENT_BEHAVIOUR_SCHEME_KEY, marketClientBehaviourScheme);
        // ---   ---

        return behaviourSchemes;
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Random random = new Random(1);
        if (!Files.exists(Paths.get(RESULTS_DIRECTORY))) {
            new File(RESULTS_DIRECTORY).mkdir();
        }

        // -------- WALLS --------
        List<Wall> walls = CSVHandler.importWalls(CSV_DIRECTORY + "/WALLS.csv");
        Simulation.createStaticFile(RESULTS_DIRECTORY, walls);

        // -------- EXITS --------
        Map<String, List<Exit>> exitsMap = CSVHandler.importExits(CSV_DIRECTORY + "/EXITS.csv");

        // -------- GRAPH --------
        Graph graph = new Graph(walls, exitsMap.values().stream().flatMap(List::stream)
                .map(Exit::getExitWall).collect(Collectors.toList()), new Vector(1, 1)); // 18,18 in new map

//         FOR GRAPH NODES PLOT  -------- DEBUGGING --------
        graph.generateOutput("./out/");
//        NodePath path = graph.AStar(graph.getClosestAccessibleNode(new Vector(6.542717913594863,-.5), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS),
//                new Vector(25., 5.0), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS);
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParametersParser parameters = new SimulationParametersParser( "./data/parameters.json", random);

        // -------- TARGETS --------
        Map<String, List<Target>> targetsMap =
                CSVHandler.importTargets(CSV_DIRECTORY + "/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- SERVERS --------
        Map<String, List<Server>> serversMap =
                CSVHandler.importServers(CSV_DIRECTORY + "/SERVERS.csv", parameters.getServerGroupsParameters(),
                parameters.getAgentsMostPossibleMaxRadius());

        // -------- BEHAVIOUR --------
        Map<String, BehaviourScheme> behaviours = getBehaviourSchemes(graph, exitsMap, serversMap, targetsMap,
                parameters.getAgentsMostPossibleMaxRadius(), random);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> generators = CSVHandler.importAgentsGenerators(
                CSV_DIRECTORY + "/GENERATORS.csv", behaviours,
                parameters.getGeneratorsParameters(), parameters.getAgentsMostPossibleMaxRadius(), random.nextLong()
        );

        // -------- ENVIRONMENT --------
        List<Server> servers = serversMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Environment environment = new Environment(walls, servers, generators,
                exitsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()) // all exits
        );

        // -------- OTHER PARAMETERS --------
        double deltaT = parameters.getAgentsMostPossibleMinRadius() / (4 * parameters.getAgentsHighestMaxVelocity());
        if(deltaT < 0.001) {
            System.out.println("Delta too small! Reducing it to 0.001.");
            deltaT = 0.001;
        } else System.out.println("DELTA_T = " + deltaT);

        // -------- OPERATIONAL MODEL MODULE --------
        OperationalModelModule operationalModelModule = new CPMAnisotropic(environment, parameters.getAgentsMostPossibleMaxRadius(), deltaT);

        // -------- EXECUTION --------
        Simulation sim = new Simulation(parameters.getMaxTime(), environment, deltaT,
                operationalModelModule, RESULTS_DIRECTORY, random, (double) parameters.getEvacuationTime());
        sim.run();
    }
}
