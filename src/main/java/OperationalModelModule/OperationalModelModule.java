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

    void findCollisions(List<Agent> agents, Environment environment,
                        List<WallCollision> wallCollisions, List<AgentsCollision> agentsCollisions, List<Agent> nonCollisionAgents);

    void executeOperationalModelModule(List<Agent> agents,Environment environment, Random random);
}
