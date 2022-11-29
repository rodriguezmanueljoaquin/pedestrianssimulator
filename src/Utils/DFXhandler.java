package Utils;

import Walls.Wall;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class DFXhandler{
    public static List<Wall> importWallsFromDFX(String filePath){
        File wallFile;
        Scanner wallScanner;

        try {
            wallFile = new File(filePath);
            wallScanner = new Scanner(wallFile);
        } catch(Exception e){
            System.out.println("Encountered exception reading wall file, returning null. Exception: " + e);
            return null;
        }

        List<Wall> walls = new ArrayList<>();
        while (wallScanner.hasNextLine()){
            walls.add(new Wall(new Vector(Double.valueOf(wallScanner.next()),Double.valueOf(wallScanner.next())),
                    new Vector(Double.valueOf(wallScanner.next()),Double.valueOf(wallScanner.next()))));
        }
        return walls;
    }
}
