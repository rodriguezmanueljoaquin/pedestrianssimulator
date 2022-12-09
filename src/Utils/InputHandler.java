package Utils;

import Environment.Target;
import Environment.Wall;

import java.io.File;
import java.util.*;

public class InputHandler {
    public static List<Wall> importWallsFromTxt(String filePath) {
        File wallFile;
        Scanner wallScanner;

        try {
            wallFile = new File(filePath);
            wallScanner = new Scanner(wallFile);
            wallScanner.useDelimiter(",");
        } catch (Exception e) {
            System.out.println("Encountered exception reading wall file, returning null. Exception: " + e);
            return null;
        }

        List<Wall> walls = new ArrayList<>();
        List<Double> inputs = new ArrayList<>(Arrays.asList(0., 0., 0., 0., 0., 0.));
        while (wallScanner.hasNextLine()) {
            String[] tokens = wallScanner.nextLine().split(",");
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Double.parseDouble(tokens[i]));
            }
            walls.add(new Wall(new Vector(inputs.get(0), inputs.get(1)), new Vector(inputs.get(3), inputs.get(4)))); // assume z is always 0
        }

        wallScanner.close();
        return walls;
    }

    public static List<Target> importTargetFromTxt(String filePath) {
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

        List<Target> targets = new ArrayList<>();
        while (targetScanner.hasNextLine()) {
            targets.add(new Target(Integer.valueOf(targetScanner.next()),
                    new Vector(Double.valueOf(targetScanner.next()), Double.valueOf(targetScanner.next())),
                    attendingTime));
        }

        Collections.shuffle(targets);

        return targets;
    }
}
