package InputHandling;

import java.io.File;
import java.util.Scanner;

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

    public static Scanner getScannerFromSecondLine(String filePath) {
        Scanner scanner = getScanner(filePath);
        scanner.nextLine();
        return scanner;
    }

    public static String getSpecificLineOfFile(String filePath, int index) {
        Scanner scanner = getScanner(filePath);
        int i = 0;
        String answer = scanner.nextLine();
        while (i < index && scanner.hasNext()) {
            answer = scanner.nextLine();
            i++;
        }
        scanner.close();
        return answer;
    }

}
