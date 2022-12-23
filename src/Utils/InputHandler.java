package Utils;

import AgentsBehaviour.BehaviourScheme;
import AgentsGenerator.AgentsGenerator;
import AgentsGenerator.AgentsGeneratorZone;
import Environment.Exit;
import Environment.Objective;
import Environment.Target;
import Environment.Wall;

import java.io.File;
import java.util.*;
import java.util.function.Function;

public class InputHandler {
    private static void readTxtWallsAndApplyFunction(String filePath, Function<Wall, Void> function) {
        Scanner scanner = getCSVScanner(filePath);

        List<Double> inputs = new ArrayList<>(Arrays.asList(0., 0., 0., 0., 0., 0.));
        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(",");
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Double.parseDouble(tokens[i]));
            }

            function.apply(new Wall(new Vector(inputs.get(0), inputs.get(1)), new Vector(inputs.get(3), inputs.get(4))));
        }

        scanner.close();
    }

    public static List<Exit> importExitsFromTxt(String filePath) {
        List<Exit> result = new ArrayList<>();
        InputHandler.readTxtWallsAndApplyFunction(filePath, (Wall wall) -> {
            result.add(new Exit(wall));
            return null;
        });

        return result;
    }

    public static List<Wall> importWallsFromTxt(String filePath) {
        List<Wall> result = new ArrayList<>();
        InputHandler.readTxtWallsAndApplyFunction(filePath, (Wall wall) -> {
            result.add(wall);
            return null;
        });

        return result;
    }

    private static AgentsGenerator createAgentsGenerator(List<Double> xInputs, List<Double> yInputs, BehaviourScheme behaviourScheme) {
        // TODO: SHOULD RECEIVE BEHAVIOUR MODULE WITH AGENTS GENERATORS PARAMETERS AND POSSIBLE TARGETS, IT SHOULDNT RECEIVE IT AS A PARAMETER
        AgentsGeneratorZone zone = new AgentsGeneratorZone(
                // rectangle is defined by its lowest and leftest point and highest and rightest point, data is assured to provide rectangles as generators zones
                new Vector(Collections.min(xInputs), Collections.min(yInputs)),
                new Vector(Collections.max(xInputs), Collections.max(yInputs))
        );

        return new AgentsGenerator(zone, 2, 50, 1, 1, 2, behaviourScheme);
    }

    public static List<AgentsGenerator> importAgentsGeneratorsFromTxt(String filePath, BehaviourScheme behaviourScheme) {
        Scanner scanner = getCSVScanner(filePath);

        List<AgentsGenerator> result = new ArrayList<>();
        List<Double> xInputs = new ArrayList<>();
        List<Double> yInputs = new ArrayList<>();
        int sidesAnalyzed = 0;
        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(",");
            // save all x inputs and y inputs separately
            for (int i = 0; i < 6; i++) {
                if (i % 3 == 0)
                    xInputs.add(Double.parseDouble(tokens[i]));
                else if (i % 3 == 1) {
                    yInputs.add(Double.parseDouble(tokens[i]));
                }
            }
            sidesAnalyzed++;

            if (sidesAnalyzed > 3) {
                sidesAnalyzed = 0;
                result.add(createAgentsGenerator(xInputs, yInputs, behaviourScheme));
                xInputs.clear();
                yInputs.clear();
            }
        }

        if (sidesAnalyzed != 0) {
            throw new RuntimeException("AgentsGenerators creation found extra lines on DXF.");
        }

        scanner.close();
        return result;
    }

    public static List<Objective> importTargetsFromTxt(String filePath) {
        //TODO: Ver cuanto le pones de attending time, que tipo de distribucion?
        Double attendingTime = 5.;
        File targetFile;
        Scanner targetScanner;

        try {
            targetFile = new File(filePath);
            targetScanner = new Scanner(targetFile);
        } catch (Exception e) {
            System.out.println("Encountered exception reading target file, returning null. Exception:" + e);
            return null;
        }

        List<Objective> targets = new ArrayList<>();
        while (targetScanner.hasNextLine()) {
            targets.add(new Target(Integer.valueOf(targetScanner.next()),
                    new Vector(Double.valueOf(targetScanner.next()), Double.valueOf(targetScanner.next())),
                    attendingTime));
        }

        Collections.shuffle(targets);

        return targets;
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
