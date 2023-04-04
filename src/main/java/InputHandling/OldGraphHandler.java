package InputHandling;

import GraphGenerator.Node;
import Utils.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static InputHandling.FileHandlers.getScanner;

public class OldGraphHandler {
    public static Map<Integer, Node> getGraphNodes(String filePath) {
        Scanner scanner = getScanner(filePath);
        scanner.nextLine(); // skip HEADERS
        Map<Integer, Node> nodes = new HashMap<>();

        while (scanner.hasNextLine()) {
            String[] row = scanner.nextLine().split(",");
            Vector pos = new Vector(Double.parseDouble(row[1]), Double.parseDouble(row[2]));
            Integer id = Integer.parseInt(row[0]);
            nodes.put(id,
                    new Node(
                            id,
                            pos,
                            Arrays.stream(row).skip(3).map(Integer::parseInt).collect(Collectors.toList())
                    )
            );
        }

        return nodes;
    }
}
