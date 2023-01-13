import Agent.Agent;
import Agent.AgentConstants;
import Agent.AgentStates;
import Environment.Environment;
import Environment.Wall;
import OperationalModelModule.CPM;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.CollisionsFinder;
import OperationalModelModule.Collisions.WallCollision;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {
    private List<Agent> agents;
    private double time, maxTime, dt, dt2;
    private Environment environment;
    private PrintWriter writer;
    private Random random;

    public Simulation(double maxTime, Environment environment, String outputDirectoryPath, Random random) throws FileNotFoundException, UnsupportedEncodingException {
        this.agents = new ArrayList<>();
        this.maxTime = maxTime;
        this.environment = environment;
        this.random = random;
        this.time = 0;
        this.dt = AgentConstants.MAX_RADIUS / (2 * AgentConstants.STANDARD_VELOCITY);
        this.dt2 = this.dt * 4;

        createDynamicFile(outputDirectoryPath);
    }

    private void executeOperationalModelModule() {
        List<WallCollision> wallCollisions = new ArrayList<>();
        List<AgentsCollision> agentsCollisions = new ArrayList<>();
        List<Agent> nonCollisionAgents = new ArrayList<>();
        CollisionsFinder.Find(this.agents, this.environment, wallCollisions, agentsCollisions, nonCollisionAgents);

        for (AgentsCollision agentsCollision : agentsCollisions) {
            CPM.updateCollidingAgents(agentsCollision);
        }

        for (WallCollision wallCollision : wallCollisions) {
            CPM.updateWallCollidingAgent(wallCollision);
        }

        // update agents not in collisions and moving
        List<Agent> movingAgents = nonCollisionAgents.stream().filter(a -> a.getState().getVelocity() != 0).collect(Collectors.toList());
        for (Agent movingAgent : movingAgents) {
            CPM.updateNonCollisionAgent(movingAgent, this.agents, this.environment, this.dt, this.random);
        }
    }

    public static void createStaticFile(String outputPath, Environment environment) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + "/static.txt", "UTF-8");

        // walls
        writer.write(String.format(Locale.ENGLISH, "%d\n", environment.getWalls().size()));
        for (Wall wall : environment.getWalls())
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
                if (agent.getState() == AgentStates.LEAVING)
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
