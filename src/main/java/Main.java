import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.PredefinedBehaviours.MarketClientBehaviourScheme;
import AgentsBehaviour.PredefinedBehaviours.SRECStudentBehaviourScheme;
import AgentsGenerator.AgentsGenerator;
import Environment.Environment;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Target.Target;
import Environment.Wall;
import GraphGenerator.Graph;
import GraphGenerator.Node;
import InputHandling.EnvironmentHandler;
import InputHandling.FileHandlers;
import InputHandling.OldGraphHandler;
import InputHandling.ParametersNames;
import InputHandling.SimulationParameters.SimulationParametersParser;
import OperationalModelModule.CPMAnisotropic;
import OperationalModelModule.OperationalModelModule;
import Utils.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String RESULTS_DIRECTORY = "./out/";
    private static final String INPUT_DIRECTORY = "./tmp/simulation_input";
    private static final String GRAPH_BACKUP_DIRECTORY = "./tmp/graph_backup";

    private static Map<String, BehaviourScheme> getPredefinedBehaviourSchemes(Graph graph, Map<String, List<Exit>> exitsMap,
                                                                              Map<String, List<Server>> serversMap, Map<String, List<Target>> targetsMap,
                                                                              double agentsMaximumMostPossibleRadius, Random random) {
        List<Exit> exits = exitsMap.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("EMERGENCY"))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream).collect(Collectors.toList());

        Map<String, BehaviourScheme> behaviourSchemes = new HashMap<>();
        behaviourSchemes.put(ParametersNames.MARKET_CLIENT_BEHAVIOUR_SCHEME_KEY,
                MarketClientBehaviourScheme.get(graph, exits, serversMap, targetsMap, agentsMaximumMostPossibleRadius, random));
        behaviourSchemes.put(ParametersNames.SREC_STUDENT_BEHAVIOUR_SCHEME_KEY,
                SRECStudentBehaviourScheme.get(graph, exits, serversMap, targetsMap, agentsMaximumMostPossibleRadius, random));

        return behaviourSchemes;
    }

    private static Graph createOrImportGraph(List<Wall> walls, Map<String, List<Exit>> exitsMap) {
        String newCSVName = FileHandlers.getSecondLineOfFile(INPUT_DIRECTORY + Graph.dxfDataFileName);
        Path graphBackupDir = Paths.get(GRAPH_BACKUP_DIRECTORY);
        if (Files.exists(graphBackupDir) &&
                Objects.equals(newCSVName, FileHandlers.getSecondLineOfFile(GRAPH_BACKUP_DIRECTORY + Graph.dxfDataFileName))) {
            // backup exists and dxf shares the same name
            Map<Integer, Node> nodes = OldGraphHandler.getGraphNodes(GRAPH_BACKUP_DIRECTORY + Graph.graphBackupFileName);
            return new Graph(nodes, walls, newCSVName);
        } else {
            Graph graph = new Graph(walls, exitsMap.values().stream().flatMap(List::stream)
                    .map(Exit::getExitWall).collect(Collectors.toList()), new Vector(18, 18), newCSVName); // 18,18 in new map; 1,1 in previous

            if (!Files.exists(graphBackupDir)) {
                new File(GRAPH_BACKUP_DIRECTORY).mkdir();
            }
            graph.generateOutput(GRAPH_BACKUP_DIRECTORY);
//         FOR GRAPH NODES PLOT  -------- DEBUGGING OF PATH --------
//        NodePath path = graph.AStar(graph.getClosestAccessibleNode(new Vector(6.542717913594863,-.5), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS),
//                new Vector(25., 5.0), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS);
//        System.out.println(path);
            return graph;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Random random = new Random(1);
        if (!Files.exists(Paths.get(RESULTS_DIRECTORY))) {
            new File(RESULTS_DIRECTORY).mkdir();
        }

        // -------- WALLS --------
        List<Wall> walls = EnvironmentHandler.importWalls(INPUT_DIRECTORY + "/WALLS.csv");
        Simulation.createStaticFile(RESULTS_DIRECTORY, walls);

        // -------- EXITS --------
        Map<String, List<Exit>> exitsMap = EnvironmentHandler.importExits(INPUT_DIRECTORY + "/EXITS.csv");

        // -------- GRAPH --------
        Graph graph = createOrImportGraph(walls, exitsMap);
//         FOR GRAPH NODES PLOT  -------- DEBUGGING OF PATH --------
//        NodePath path = graph.AStar(graph.getClosestAccessibleNode(new Vector(6.542717913594863,-.5), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS),
//                new Vector(25., 5.0), AgentConstants.MAX_RADIUS_OF_ALL_AGENTS);
//        System.out.println(path);

        // -------- CONFIGURATION --------
        SimulationParametersParser parameters = new SimulationParametersParser(INPUT_DIRECTORY + "/parameters.json", random);

        // -------- TARGETS --------
        Map<String, List<Target>> targetsMap =
                EnvironmentHandler.importTargets(INPUT_DIRECTORY + "/TARGETS.csv", parameters.getTargetGroupsParameters());

        // -------- SERVERS --------
        Map<String, List<Server>> serversMap =
                EnvironmentHandler.importServers(INPUT_DIRECTORY + "/SERVERS.csv", parameters.getServerGroupsParameters(),
                        parameters.getAgentsMostPossibleMaxRadius());

        // -------- BEHAVIOUR --------
        Map<String, BehaviourScheme> behaviours = getPredefinedBehaviourSchemes(graph, exitsMap, serversMap, targetsMap,
                parameters.getAgentsMostPossibleMaxRadius(), random);

        // -------- AGENT GENERATORS --------
        List<AgentsGenerator> generators = EnvironmentHandler.importAgentsGenerators(
                INPUT_DIRECTORY + "/GENERATORS.csv", behaviours,
                parameters.getGeneratorsParameters(), parameters.getAgentsMostPossibleMaxRadius(), random.nextLong()
        );

        // -------- ENVIRONMENT --------
        List<Server> servers = serversMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        Environment environment = new Environment(walls, servers, generators,
                exitsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()) // all exits
        );

        // -------- OTHER PARAMETERS --------
        double deltaT = parameters.getAgentsMostPossibleMinRadius() / (4 * parameters.getAgentsHighestMaxVelocity());
        if (deltaT < 0.001) {
            System.out.println("Delta too small! Reducing it to 0.001.");
            deltaT = 0.001;
        }

        // -------- OPERATIONAL MODEL MODULE --------
        OperationalModelModule operationalModelModule = new CPMAnisotropic(environment, parameters.getAgentsMostPossibleMaxRadius(), deltaT);

        // -------- EXECUTION --------
        Simulation sim = new Simulation(parameters.getMaxTime(), environment, graph, deltaT,
                operationalModelModule, RESULTS_DIRECTORY, random, (double) parameters.getEvacuationTime());
        sim.run();
    }
}
