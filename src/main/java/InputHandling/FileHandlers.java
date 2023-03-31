package InputHandling;

import Environment.Objectives.Exit;
import Environment.Wall;
import Utils.Vector;

import java.io.File;
import java.util.*;

public class FileHandlers {
    public static Scanner getScanner(String filePath) {
        File file;
        Scanner scanner;
        try {
            file = new File(filePath);
            scanner = new Scanner(file);
        } catch (Exception e) {
            throw new RuntimeException("Encountered exception when trying to read file " + filePath);
        }

        return scanner;
    }

    public static String getSecondLineOfFile(String filePath) {
        Scanner scanner = getScanner(filePath);
        String answer = scanner.nextLine();
        scanner.close();
        return answer;
    }


    public static Map<String, List<Exit>> importExits(String filePath) {
        Map<String, List<Exit>> exitsMap = new HashMap<>();

        Scanner scanner = getScanner(filePath);
        while (scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");
            Utils.Vector start = new Utils.Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2]));
            Utils.Vector end = new Vector(Double.parseDouble(row[4]), Double.parseDouble(row[5]));

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

    public static List<Wall> importWalls(String filePath) {
        List<Wall> result = new ArrayList<>();
        Scanner scanner = getScanner(filePath);

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
}
