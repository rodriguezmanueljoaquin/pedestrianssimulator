package Utils;

import Environment.Target;
import Environment.Wall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DFXHandler {
    public static List<Wall> importWallsFromTxt(String filePath) {
        File wallFile;
        Scanner wallScanner;

        try {
            wallFile = new File(filePath);
            wallScanner = new Scanner(wallFile);
        } catch (Exception e) {
            System.out.println("Encountered exception reading wall file, returning null. Exception: " + e);
            return null;
        }

        List<Wall> walls = new ArrayList<>();
        while (wallScanner.hasNextLine()) {
            walls.add(new Wall(new Vector(Double.valueOf(wallScanner.next()), Double.valueOf(wallScanner.next())),
                    new Vector(Double.valueOf(wallScanner.next()), Double.valueOf(wallScanner.next()))));
        }
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
