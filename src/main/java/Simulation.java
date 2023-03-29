import Agent.Agent;
import Agent.AgentStates;
import Environment.Environment;
import Environment.Wall;
import OperationalModelModule.OperationalModelModule;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Simulation {
    private final List<Agent> agents, leavingAgents;
    private final double maxTime;
    private final double dt;
    private final Environment environment;
    private final Random random;
    private final OperationalModelModule operationalModelModule;
    private final Double evacuationTime;
    private double time;
    private boolean alreadyEvacuating = false;
    private PrintWriter writer;

    public Simulation(double maxTime, Environment environment, double deltaT,
                      OperationalModelModule operationalModelModule,
                      String outputDirectoryPath, Random random, Double evacuationTime) throws FileNotFoundException, UnsupportedEncodingException {
        this.maxTime = maxTime;
        this.environment = environment;
        this.operationalModelModule = operationalModelModule;
        this.random = random;
        this.evacuationTime = evacuationTime;
        this.agents = new ArrayList<>();
        this.leavingAgents = new ArrayList<>();
        this.time = 0;
        this.dt = deltaT;

        createDynamicFile(outputDirectoryPath);
    }

    public static void createStaticFile(String outputPath, List<Wall> walls) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file.");

        PrintWriter writer = new PrintWriter(outputPath + "/static.txt", "UTF-8");

        // walls
        writer.write(String.format(Locale.ENGLISH, "%d\n", walls.size()));
        for (Wall wall : walls)
            writer.write(String.format(Locale.ENGLISH, "%s\n", wall.toString()));

        writer.close();
        System.out.println("\tStatic file successfully created.");
    }

    private List<Agent> getAllAgents() {
        return Stream.concat(this.agents.stream(), this.leavingAgents.stream()).collect(Collectors.toList());
    }

    private void updateEnvironment() {
        List<Agent> newAgents = this.environment.update(this.time, this.agents);
        this.agents.addAll(newAgents);
    }

    // returns different from zero when all agents have evacuated
    private int manageEvacuation() {
        if (!this.alreadyEvacuating) {
            if (this.time >= this.evacuationTime) {
                this.alreadyEvacuating = true;
                for (Agent agent : this.agents) {
                    agent.evacuate(this.environment.getExits());
                }
            } else {
                // still not evacuation time, update as usual
                this.updateEnvironment();
            }
        } else {
            if (this.agents.size() == 0)
                // stop simulation
                return 1;
        }
        return 0;
    }

    public void run() {
        System.out.println("\t\tSimulation started.");
        // first iteration
        this.writeOutput();
        double nextPercentage = 0.1;
        while (this.time < this.maxTime) {
            // create new agents and update

            if (this.evacuationTime != null) {
                if (this.manageEvacuation() != 0) break;
            } else {
                this.updateEnvironment();
            }

            // update positions and state
            for (Agent agent : this.getAllAgents()) {
                agent.updatePosition(this.dt);
                agent.getStateMachine().updateAgent(agent, this.time);
            }

            this.checkLeavingAgents();

            this.operationalModelModule.executeOperationalModelModule(agents, environment, random);

            // escribir output
            this.writeOutput();
            this.time += this.dt;

            if (this.time > nextPercentage * this.maxTime) {
                System.out.printf("\t\t\t%d%% percentage of the simulation finished.\n", (int) Math.ceil(nextPercentage * 100));
                nextPercentage += 0.1;
            }
        }
        this.writer.close();
        System.out.println("\t\tSimulation ended.");
        System.out.println("\tSuccesfully created dynamic file.");
    }

    private void checkLeavingAgents() {
        List<Agent> leftAgents = new ArrayList<>();
        for (Agent agent : this.leavingAgents) {
            if (agent.getState() == AgentStates.LEFT) {
                leftAgents.add(agent);
            }
        }
        this.leavingAgents.removeAll(leftAgents);

        for (Agent agent : this.agents) {
            if (agent.getState() == AgentStates.LEAVING) {
                this.leavingAgents.add(agent);
            } else agent.updateDirection();
        }
        this.agents.removeAll(leavingAgents);
    }


    private void writeOutput() {
        List<Agent> allAgents = this.getAllAgents();
        this.writer.write(String.format(Locale.ENGLISH, "%f;%d\n", this.time, allAgents.size()));
        for (Agent agent : allAgents) {
            this.writer.write(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f;%f;%d\n",
                    agent.getId(), agent.getPosition().getX(), agent.getPosition().getY(), agent.getVelocity().getX(),
                    agent.getVelocity().getY(), agent.getRadius(), agent.getState().ordinal()));
        }
        this.writer.write("\n"); // end of iterations
    }

    private void createDynamicFile(String outputDirectoryPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file.");
        this.writer = new PrintWriter(outputDirectoryPath + "/dynamic.txt", "UTF-8");
    }
}
