package InputHandling.EnvironmentData;

import AgentsBehaviour.BehaviourScheme;
import AgentsGenerator.AgentsGenerator;
import AgentsGenerator.AgentsGeneratorZone;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.DynamicServer;
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
        Map<String, List<Server>> servers = new HashMap<>();
        for (String key : serverGroupsParameters.keySet()) {
            servers.put(key, new ArrayList<>());
        }

        Scanner scanner = getCSVScanner(filePath);
        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(",");

            String name = tokens[0];
            int delimiterIndex = name.indexOf("_");
            String serverGroupId = name.substring(0, delimiterIndex);
            ServerGroupParameters serverGroupParameters = serverGroupsParameters.get(serverGroupId);

            if (serverGroupParameters == null)
                throw new RuntimeException("No parameters found for server group: " + serverGroupId);

            servers.get(serverGroupId).add(
                    createServer(serverGroupParameters, tokens)
            );
        }

        scanner.close();
        return servers;
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

    private static Server createServer(ServerGroupParameters serverGroupParameters, String[] tokens) {
        Server server;
        Rectangle area = new Rectangle(
                new Utils.Vector(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])),
                new Utils.Vector(Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]))
        );
        String name = tokens[0]; // format X_Y_Z where X is the groupId, and Z is the queueId in case of existence
        int firstDelimiterIndex = name.indexOf("_");

        if (serverGroupParameters.hasQueue()) {
            int lastDelimiterIndex = name.lastIndexOf("_");
            String queueID = name.substring(lastDelimiterIndex + 1);
            server = new DynamicServer(
                    name.substring(firstDelimiterIndex + 1, lastDelimiterIndex),
                    serverGroupParameters.getMaxCapacity(),
                    area,
                    serverGroupParameters.getAttendingTime(),
                    serverGroupParameters.getQueue(queueID)
            );
        } else {
            server = new StaticServer(
                    name.substring(firstDelimiterIndex + 1),
                    serverGroupParameters.getMaxCapacity(),
                    area,
                    serverGroupParameters.getStartTime(),
                    serverGroupParameters.getAttendingTime()
            );
        }

        return server;
    }

}
