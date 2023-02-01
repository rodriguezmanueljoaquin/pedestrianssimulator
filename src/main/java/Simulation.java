import Agent.Agent;
import Agent.AgentStates;
import Environment.Environment;
import Environment.Wall;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.CollisionsFinder;
import OperationalModelModule.Collisions.WallCollision;
import OperationalModelModule.OperationalModelModule;
import Utils.Constants;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static Utils.Constants.DELTA_T;

public class Simulation {
    private final List<Agent> agents;
    private final double maxTime;
    private final double dt;
    private final Environment environment;
    private final Random random;
    private final OperationalModelModule operationalModelModule;
    private double time;
    private PrintWriter writer;

    public Simulation(double maxTime, Environment environment, OperationalModelModule operationalModelModule,
                      String outputDirectoryPath, Random random) throws FileNotFoundException, UnsupportedEncodingException {
        this.maxTime = maxTime;
        this.environment = environment;
        this.operationalModelModule = operationalModelModule;
        this.random = random;
        this.agents = new ArrayList<>();
        this.time = 0;
        this.dt = DELTA_T;

        createDynamicFile(outputDirectoryPath);
    }

    public static void createStaticFile(String outputPath, List<Wall> walls) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + "/static.txt", "UTF-8");

        // walls
        writer.write(String.format(Locale.ENGLISH, "%d\n", walls.size()));
        for (Wall wall : walls)
            writer.write(String.format(Locale.ENGLISH, "%s\n", wall.toString()));

        writer.close();
        System.out.println("\tStatic file successfully created");
    }

    public void run() {
        System.out.println("\t\tSimulation started...");
        // first iteration
        this.writeOutput();

        while (this.time < this.maxTime) {
            // create new agents and update servers
            this.agents.addAll(this.environment.update(this.time));

            // update positions and state
            for (Agent agent : this.agents) {
                agent.updatePosition(this.dt);
                agent.getStateMachine().updateAgent(agent, this.time);
            }

            // remove agents that left and update the velocity of the rest
            List<Agent> leavingAgents = new ArrayList<>();
            for (Agent agent : this.agents) {
                if (agent.getState() == AgentStates.LEAVING && this.time - agent.getStartedAttendingAt() > Constants.LEAVING_TIME)
                    leavingAgents.add(agent);
                else
                    agent.updateVelocity();
            }
            this.agents.removeAll(leavingAgents);

            this.executeOperationalModelModule();

            // escribir output
            this.writeOutput();
            this.time += this.dt;
        }
        this.writer.close();
        System.out.println("\t\tSimulation ended");
        System.out.println("\tSuccesfully created dynamic file");
    }


    private void executeOperationalModelModule() {
        this.operationalModelModule.updateAgentsPosition(this.agents);
        List<WallCollision> wallCollisions = new ArrayList<>();
        List<AgentsCollision> agentsCollisions = new ArrayList<>();
        List<Agent> nonCollisionAgents = new ArrayList<>();
        CollisionsFinder.Find(this.agents, this.environment, wallCollisions, agentsCollisions, nonCollisionAgents);

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

            if (agent.getState().getVelocity() != 0)
                // if moving, update direction with heuristics
                this.operationalModelModule.updateNonCollisionAgent(agent, this.dt, this.random);
        }
    }

    private void writeOutput() {
        this.writer.write(String.format(Locale.ENGLISH, "%f;%d\n", this.time, agents.size()));
        for (Agent agent : this.agents) {
            this.writer.write(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f;%f;%d\n",
                    agent.getId(), agent.getPosition().getX(), agent.getPosition().getY(), agent.getVelocity().getX(),
                    agent.getVelocity().getY(), agent.getRadius(), agent.getState().ordinal()));
        }
        this.writer.write("\n"); // end of iterations
        this.writer.flush();
    }

    private void createDynamicFile(String outputDirectoryPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        this.writer = new PrintWriter(outputDirectoryPath + "/dynamic.txt", "UTF-8");
    }
}
