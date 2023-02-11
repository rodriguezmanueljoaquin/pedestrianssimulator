package InputHandling.EnvironmentData;

import AgentsBehaviour.BehaviourScheme;
import AgentsGenerator.AgentsGenerator;
import AgentsGenerator.AgentsGeneratorZone;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.DynamicServer;
import Environment.Objectives.Server.QueueLine;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Server.StaticServer;
import Environment.Objectives.Target;
import Environment.Wall;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.ServerGroupParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.TargetGroupParameters;
import Utils.Rectangle;
import Utils.Vector;

import java.io.File;
import java.util.*;
import java.util.function.Function;

public class CSVHandler {
    private static void readWallsAndApplyFunction(String filePath, Function<Wall, Void> function) {
        Scanner scanner = getCSVScanner(filePath);

        List<Double> inputs = new ArrayList<>(Arrays.asList(0., 0., 0., 0., 0., 0.));
        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(",");
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Double.parseDouble(tokens[i]));
            }

            function.apply(new Wall(new Utils.Vector(inputs.get(0), inputs.get(1)), new Utils.Vector(inputs.get(3), inputs.get(4))));
        }

        scanner.close();
    }

    public static List<Exit> importExits(String filePath) {
        List<Exit> result = new ArrayList<>();
        CSVHandler.readWallsAndApplyFunction(filePath, (Wall wall) -> {
            result.add(new Exit(wall));
            return null;
        });

        return result;
    }

    public static List<Wall> importWalls(String filePath) {
        List<Wall> result = new ArrayList<>();
        CSVHandler.readWallsAndApplyFunction(filePath, (Wall wall) -> {
            result.add(wall);
            return null;
        });

        return result;
    }

    public static List<AgentsGenerator> importAgentsGenerators(String filePath,
                                                               Map<String, BehaviourScheme> possibleBehaviourSchemes,
                                                               Map<String, AgentsGeneratorParameters> generatorsParameters, long randomSeed) {
        List<AgentsGenerator> generators = new ArrayList<>();
        Scanner scanner = getCSVScanner(filePath);

        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(",");

            AgentsGeneratorZone zone = new AgentsGeneratorZone(
                    new Utils.Vector(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])),
                    new Utils.Vector(Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]))
            );

            String generatorGroupId = tokens[0];

            AgentsGeneratorParameters agentsGeneratorParameters = generatorsParameters.get(generatorGroupId);
            if (agentsGeneratorParameters == null)
                throw new RuntimeException("No parameters found for agent generator group: " + generatorGroupId);

            BehaviourScheme behaviourScheme = possibleBehaviourSchemes.get(agentsGeneratorParameters.getBehaviourSchemeKey());
            if (behaviourScheme == null)
                throw new RuntimeException("Behaviour scheme: '" + agentsGeneratorParameters.getBehaviourSchemeKey() + "' not found.");

            generators.add(
                    new AgentsGenerator(
                            generatorGroupId, zone, agentsGeneratorParameters,
                            behaviourScheme, randomSeed
                    )
            );
        }
        scanner.close();

        return generators;
    }

    public static Map<String, List<Target>> importTargets(String filePath, Map<String, TargetGroupParameters> targetGroupsParameters) {
        Map<String, List<Target>> targets = new HashMap<>();
        for (String key : targetGroupsParameters.keySet()) {
            targets.put(key, new ArrayList<>());
        }

        Scanner scanner = getCSVScanner(filePath);
        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(",");

            String targetGroupId = tokens[0];
            TargetGroupParameters targetGroupParameters = targetGroupsParameters.get(targetGroupId);

            if (targetGroupParameters == null)
                throw new RuntimeException("No parameters found for target group: " + targetGroupId);

            targets.get(targetGroupId).add(
                    new Target(
                            targetGroupId,
                            new Vector(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])),
                            targetGroupParameters.getAttendingTime(),
                            Double.parseDouble(tokens[4])
                    )
            );
        }

        scanner.close();
        return targets;
    }

    public static Map<String, List<Server>> importServers(String filePath, Map<String, ServerGroupParameters> serverGroupsParameters) {
        Map<String, List<Server>> serversMap = new HashMap<>();
        for (String key : serverGroupsParameters.keySet()) {
            serversMap.put(key, new ArrayList<>());
        }

        Scanner scanner = getCSVScanner(filePath);
        // First, sort CSV rows so that we have the queue line before its server line that has the same name, but _SERVER instead of _QUEUE at the end
        List<String[]> rows = new ArrayList<>();
        while (scanner.hasNextLine()) {
            rows.add(scanner.nextLine().split(","));
        }
        scanner.close();

        rows.sort(Comparator.comparing(o -> o[0]));
        findServers(serversMap, rows, serverGroupsParameters);

        return serversMap;
    }

    private static void findServers(Map<String, List<Server>> serversMap, List<String[]> rows, Map<String, ServerGroupParameters> serverGroupsParameters) {
        QueueLine queue = null;
        for (String[] row : rows) {
            Vector start = new Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2]));
            Vector end = new Vector(Double.parseDouble(row[4]), Double.parseDouble(row[5]));

            // in CSV servers first element follows the format: NAME_ID_TYPE
            String serverGroupId = row[0].substring(0, row[0].indexOf('_'));
            String name = row[0].substring(serverGroupId.length() + 1, row[0].lastIndexOf('_'));
            String type = row[0].substring(serverGroupId.length() + 1 + name.length() + 1);

            ServerGroupParameters serverGroupParameters = serverGroupsParameters.get(serverGroupId);
            if (serverGroupParameters == null)
                throw new RuntimeException("No parameters found for server group: " + serverGroupId);

            switch (type) {
                case "SERVER":
                    Server server;
                    Rectangle area = new Rectangle(start, end);
                    if (queue != null) {
                        server = new DynamicServer(
                                name,
                                serverGroupParameters.getMaxCapacity(),
                                area,
                                serverGroupParameters.getAttendingTime(),
                                queue
                        );
                        queue = null;
                    } else {
                        server = new StaticServer(
                                name,
                                serverGroupParameters.getMaxCapacity(),
                                area,
                                serverGroupParameters.getStartTime(),
                                serverGroupParameters.getAttendingTime()
                        );
                    }

                    serversMap.get(serverGroupId).add(
                            server
                    );
                    break;

                case "QUEUE":
                    if (queue != null)
                        throw new RuntimeException("ERROR IN SERVERS.csv queue without server associated");

                    queue = new QueueLine(start, end);
                    break;

                default:
                    throw new RuntimeException("ERROR IN SERVERS.csv on row: " + Arrays.toString(row));
            }
        }
    }

    private static Scanner getCSVScanner(String filePath) {
        File file;
        Scanner scanner;
        try {
            file = new File(filePath);
            scanner = new Scanner(file);
            scanner.useDelimiter(",");
        } catch (Exception e) {
            throw new RuntimeException("Encountered exception when trying to read file " + filePath);
        }

        return scanner;
    }

}
