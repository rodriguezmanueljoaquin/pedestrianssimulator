import Agent.Agent;
import Agent.AgentStates;
import Environment.Environment;
import Environment.Server.Server;
import Environment.Wall;
import GraphGenerator.Graph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Simulation {
    private List<Agent> agents;
    private double time, maxTime, dt, dt2;
    private Environment environment;
    private PrintWriter writer;
    private Graph graph;

    public Simulation(Graph graph, double maxTime, double dt, double dt2, Environment environment, String outputDirectoryPath) throws FileNotFoundException, UnsupportedEncodingException {
        this.agents = new ArrayList<>();
        this.maxTime = maxTime;
        this.dt = dt;
        this.dt2 = dt2;
        this.graph = graph;
        this.environment = environment;
        this.time = 0;
        createDynamicFile(outputDirectoryPath);
    }

    public void run() {
        System.out.println("\t\tSimulation started...");
        // first iteration
        this.writeOutput();

        while (this.time < this.maxTime) {
            // create new agents
            this.agents.addAll(this.environment.generateAgents(this.time));

            // TODO: make environment check servers and free its users if necessary?¿

            // update positions
            for (Agent agent : this.agents) {
                agent.updatePosition(this.dt);
                agent.getStateMachine().updateAgent(agent, this.time);
            }
            // update servers
            for (Server server : this.environment.getServers()) {
                server.updateServer();
            }
            // remove agents that left
            List<Agent> leavingAgents = new ArrayList<>();
            for (Agent agent : this.agents) {
                if (agent.getState() == AgentStates.LEAVING)
                    leavingAgents.add(agent);
                agent.updateVelocity();
            }
            this.agents.removeAll(leavingAgents);


            // update velocities acording CPM
            CPM.updateAgents(agents, environment);


            // escribir output
            this.writeOutput();
            this.time += this.dt;
        }
        this.writer.close();
        System.out.println("\t\tSimulation ended");
        System.out.println("\tSuccesfully created dynamic file");
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
