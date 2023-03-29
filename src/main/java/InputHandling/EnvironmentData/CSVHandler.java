package InputHandling.EnvironmentData;

import AgentsBehaviour.BehaviourScheme;
import AgentsGenerator.AgentsGenerator;
import AgentsGenerator.AgentsGeneratorZone;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.DynamicServer;
import Environment.Objectives.Server.Queue;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Server.StaticServer;
import Environment.Objectives.Target.DotTarget;
import Environment.Objectives.Target.Target;
import Environment.Wall;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.ServerGroupParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.TargetGroupParameters;
import Utils.Vector;
import Utils.*;

import java.io.File;
import java.util.*;

public class CSVHandler {
    public static List<Wall> importWalls(String filePath) {
        List<Wall> result = new ArrayList<>();
        Scanner scanner = getCSVScanner(filePath);

        List<Double> inputs = new ArrayList<>(Arrays.asList(0., 0., 0., 0., 0., 0.));
        while (scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Double.parseDouble(row[i]));
            }

            result.add(new Wall(new Utils.Vector(inputs.get(0), inputs.get(1)), new Utils.Vector(inputs.get(3), inputs.get(4))));
        }

        scanner.close();

        return result;
    }

    public static List<AgentsGenerator> importAgentsGenerators(String filePath,
                                                               Map<String, BehaviourScheme> possibleBehaviourSchemes,
                                                               Map<String, AgentsGeneratorParameters> generatorsParameters,
                                                               double agentsMaximumMostPossibleRadius,
                                                               long randomSeed) {
        List<AgentsGenerator> generators = new ArrayList<>();
        Scanner scanner = getCSVScanner(filePath);
        Random random = new Random(randomSeed);

        while (scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");

            AgentsGeneratorZone zone = new AgentsGeneratorZone(
                    new Utils.Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2])),
                    new Utils.Vector(Double.parseDouble(row[4]), Double.parseDouble(row[5])),
                    agentsMaximumMostPossibleRadius
            );

            String generatorGroupId = row[0];

            AgentsGeneratorParameters agentsGeneratorParameters = generatorsParameters.get(generatorGroupId);
            if (agentsGeneratorParameters == null)
                throw new RuntimeException("No parameters found for agent generator group: " + generatorGroupId);

            BehaviourScheme behaviourScheme = possibleBehaviourSchemes.get(agentsGeneratorParameters.getBehaviourSchemeKey());
            if (behaviourScheme == null)
                throw new RuntimeException("Behaviour scheme: '" + agentsGeneratorParameters.getBehaviourSchemeKey() + "' not found.");

            generators.add(
                    new AgentsGenerator(
                            generatorGroupId, zone, agentsGeneratorParameters, behaviourScheme, agentsMaximumMostPossibleRadius, random.nextLong()
                    )
            );
        }
        scanner.close();

        return generators;
    }

    public static Map<String, List<Exit>> importExits(String filePath) {
        Map<String, List<Exit>> exitsMap = new HashMap<>();

        Scanner scanner = getCSVScanner(filePath);
        while (scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");
            Vector start = new Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2]));
            Vector end = new Vector(Double.parseDouble(row[4]), Double.parseDouble(row[5]));

            String exitGroupId = row[0];
            if (!exitsMap.containsKey(exitGroupId))
                exitsMap.put(exitGroupId, new ArrayList<>());

            exitsMap.get(exitGroupId).add(
                    new Exit(
                            new Wall(start, end)
                    )
            );
        }

        scanner.close();
        return exitsMap;
    }

    public static Map<String, List<Target>> importTargets(String filePath, Map<String, TargetGroupParameters> targetGroupsParameters) {
        Map<String, List<Target>> targets = new HashMap<>();
        for (String key : targetGroupsParameters.keySet()) {
            targets.put(key, new ArrayList<>());
        }

        Scanner scanner = getCSVScanner(filePath);
        while (scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");

            String targetGroupId = row[0];
            TargetGroupParameters targetGroupParameters = targetGroupsParameters.get(targetGroupId);

            if (targetGroupParameters == null)
                throw new RuntimeException("No parameters found for target group: " + targetGroupId);

            Zone targetZone;
            switch (row.length) {
                case 5:
                    //Circle
                    targetZone = new Circle(new Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2])), Double.parseDouble(row[4]));
                    break;

                case 7:
                    //Rectangle
                    targetZone = new Rectangle(new Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2])),
                            new Vector(Double.parseDouble(row[4]), Double.parseDouble(row[5])));
                    break;
                default:
                    throw new RuntimeException("Target.CSV row wrong formatted: " + Arrays.toString(row));
            }

            targets.get(targetGroupId).add(new DotTarget(
                    targetGroupParameters.getAttendingTimeGenerator(),
                    targetGroupId,
                    targetZone
            ));
        }

        scanner.close();
        return targets;
    }

    public static Map<String, List<Server>> importServers(String filePath, Map<String, ServerGroupParameters> serverGroupsParameters,
                                                          double agentsMaximumMostPossibleRadius) {
        Map<String, List<Server>> serversMap = new HashMap<>();
        for (String key : serverGroupsParameters.keySet()) {
            serversMap.put(key, new ArrayList<>());
        }

        Scanner scanner = getCSVScanner(filePath);
        Map<String, List<String[]>> rowsMap = new HashMap<>();
        while (scanner.hasNextLine()) {
            // group data by server name and id, so queue and zone are together
            String[] row = scanner.nextLine().split(",");
            String fullName = row[0].substring(0, row[0].lastIndexOf('_'));
            if (!rowsMap.containsKey(fullName))
                rowsMap.put(fullName, new ArrayList<>());

            rowsMap.get(fullName).add(row);
        }
        scanner.close();

        parseServers(serversMap, rowsMap, serverGroupsParameters, agentsMaximumMostPossibleRadius);

        return serversMap;
    }

    private static void parseServers(Map<String, List<Server>> serversMap, Map<String, List<String[]>> rowsMap,
                                     Map<String, ServerGroupParameters> serverGroupsParameters, double agentsMaximumMostPossibleRadius) {
        for (String serverFullName : rowsMap.keySet()) {
            String serverGroupId = serverFullName.substring(0, serverFullName.indexOf('_'));
            ServerGroupParameters serverGroupParameters = serverGroupsParameters.get(serverGroupId);
            if (serverGroupParameters == null)
                throw new RuntimeException("No parameters found for server group: " + serverGroupId);

            List<String[]> serverRows = rowsMap.get(serverFullName);
            // sort so first QUEUE are analyzed, and then the SERVER
            serverRows.sort(Comparator.comparing(o -> o[0].charAt(serverFullName.length() + 1))); // skip '_'
            List<Line> queueLines = new ArrayList<>();
            String name = serverFullName.substring(serverGroupId.length() + 1);

            for (String[] row : serverRows) {
                Vector start = new Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2]));
                Vector end = new Vector(Double.parseDouble(row[4]), Double.parseDouble(row[5]));
                String type = row[0].substring(serverFullName.length() + 1); // skip '_'

                switch (type) {
                    case "SERVER":
                        Server server;
                        Rectangle area = new Rectangle(start, end);
                        if (queueLines.isEmpty()) {
                            server = new StaticServer(
                                    name,
                                    serverGroupParameters.getMaxCapacity(),
                                    area,
                                    serverGroupParameters.getStartTime(),
                                    serverGroupParameters.getAttendingTimeGenerator()
                            );
                        } else {
                            server = new DynamicServer(
                                    name,
                                    serverGroupParameters.getMaxCapacity(),
                                    area,
                                    serverGroupParameters.getAttendingTimeGenerator(),
                                    new Queue(queueLines, agentsMaximumMostPossibleRadius)
                            );
                        }

                        serversMap.get(serverGroupId).add(server);
                        break;

                    default:
                        if (type.startsWith("QUEUE")) {
                            queueLines.add(new Line(start, end));
                        } else throw new RuntimeException("ERROR IN SERVERS.csv on row: " + Arrays.toString(row));
                }
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
