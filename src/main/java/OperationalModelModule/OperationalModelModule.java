package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;

import java.util.List;
import java.util.Random;

public interface OperationalModelModule {
    //De esta interfaz se podrian sacar todos los metodos menos executeOperationalModelModule
    //y hacer todos los demas metodos protected
    void updateAgents(List<Agent> agents);

    void updateNonCollisionAgent(Agent agent, Random random);

    void expandAgent(Agent agent);

    void updateCollidingAgents(AgentsCollision agentsCollision);

    void updateWallCollidingAgent(WallCollision wallCollision);

    // Method that returns true if OMM considers agent is in contact with other
    boolean agentCollidesAgainst(Agent agent, Agent other);

    // Receives agents, environment, and the two list where it will store the collisions made by the agents
    void findCollisions(List<Agent> agents, Environment environment,
                        List<WallCollision> wallCollisions, List<AgentsCollision> agentsCollisions, List<Agent> nonCollisionAgents);

    void executeOperationalModelModule(List<Agent> agents, Environment environment, Random random);
}
