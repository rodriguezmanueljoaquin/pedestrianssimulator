import Agent.Agent;
import Agent.AgentStates;
import Environment.Environment;
import Environment.Wall;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.CollisionsFinder;
import OperationalModelModule.Collisions.WallCollision;
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

    public void run() {
        System.out.println("\t\tSimulation started.");
        // first iteration
        this.writeOutput();
        double nextPercentage = 0.1;
        while (this.time < this.maxTime) {
            // create new agents and update servers
            List<Agent> newAgents = this.environment.update(this.time, this.agents);

            // check for evacuation
            if (this.evacuationTime != null && this.time >= this.evacuationTime) {
                if (this.agents.size() == 0)
                    // stop simulation
                    break;

                for (Agent agent : this.agents) {
                    agent.evacuate(this.environment.getExits());
                }
            } else this.agents.addAll(newAgents);

            // update positions and state
            for (Agent agent : this.getAllAgents()) {
                agent.updatePosition(this.dt);
                agent.getStateMachine().updateAgent(agent, this.time);
            }

            this.checkLeavingAgents();

            this.executeOperationalModelModule();

            // escribir output
            this.writeOutput();
            this.time += this.dt;

            if(this.time > nextPercentage * this.maxTime) {
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

    private void executeOperationalModelModule() {
        this.operationalModelModule.updateAgents(this.agents);
        List<WallCollision> wallCollisions = new ArrayList<>();
        List<AgentsCollision> agentsCollisions = new ArrayList<>();
        List<Agent> nonCollisionAgents = new ArrayList<>();
        CollisionsFinder.FindAnisotropic(this.agents, this.environment, wallCollisions, agentsCollisions, nonCollisionAgents);

        for (AgentsCollision agentsCollision : agentsCollisions) {
            this.operationalModelModule.updateCollidingAgents(agentsCollision);
        }

        for (WallCollision wallCollision : wallCollisions) {
            this.operationalModelModule.updateWallCollidingAgent(wallCollision);
            Agent agent = wallCollision.getAgent();
            agent.getStateMachine().updateAgentCurrentPath(agent); // maybe because of this impact it has to change its path
        }

        for (Agent agent : nonCollisionAgents) {
            // update radius
            this.operationalModelModule.expandAgent(agent);

            if (agent.getState().getMaxVelocityFactor() != 0)
                // if moving, update direction with heuristics
                this.operationalModelModule.updateNonCollisionAgent(agent, this.dt, this.random);
        }
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
        this.writer.flush();
    }

    private void createDynamicFile(String outputDirectoryPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file.");
        this.writer = new PrintWriter(outputDirectoryPath + "/dynamic.txt", "UTF-8");
    }
}
