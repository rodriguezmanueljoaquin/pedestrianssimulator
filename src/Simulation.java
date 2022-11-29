import Environment.Environment;
import Environment.Wall;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

public class Simulation {
    private List<Agent> agents;
    private double time, maxTime, dt, dt2;
    private Environment environment;
    private PrintWriter writer;

    public Simulation(List<Agent> agents, double maxTime, double dt, double dt2, Environment environment, String outputDirectoryPath) throws FileNotFoundException, UnsupportedEncodingException {
        this.agents = agents;
        this.maxTime = maxTime;
        this.dt = dt;
        this.dt2 = dt2;
        this.environment = environment;
        this.time = 0;
        createDynamicFile(outputDirectoryPath);
    }

    public void run() {
        // first iteration
        this.writeOutput();

        while (this.time < maxTime) {
            // actualizar posiciones
            for (Agent agent : this.agents)
                agent.updatePosition(this.dt);

            CPM.updateAgents(agents, environment);

            // escribir output
            this.writeOutput();
            this.time += this.dt;
        }
    }

    public static void createStaticFile(String outputPath, Environment environment) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + "/static.txt", "UTF-8");

        // walls
        writer.write(String.format("%d\n", environment.getWalls().size()));
        for(Wall wall : environment.getWalls())
            writer.println(wall.toString());

        writer.close();
        System.out.println("\tStatic file successfully created");
    }

    private void writeOutput() {
        this.writer.println(this.time);
        for (Agent agent : this.agents) {
            this.writer.println(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f;%f;%d",
                    agent.getId(), agent.getPosition().getX(), agent.getPosition().getY(), agent.getVelocity().getX(),
                    agent.getVelocity().getY(), agent.getRadius(), agent.getState().ordinal()));
        }
    }

    private void createDynamicFile(String outputDirectoryPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file...");
        this.writer = new PrintWriter(outputDirectoryPath + "/dynamic.txt", "UTF-8");
        System.out.println("\tSuccesfully created dynamic file...");
    }
}
