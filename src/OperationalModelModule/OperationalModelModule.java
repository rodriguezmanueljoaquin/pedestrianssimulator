package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;

import java.util.List;
import java.util.Random;

public interface OperationalModelModule {
    void updateAgentsPosition(List<Agent> agents);

    void updateNonCollisionAgent(Agent agent, double dt, Random random);

    void expandAgent(Agent agent);

    void updateCollidingAgents(AgentsCollision agentsCollision);

    void updateWallCollidingAgent(WallCollision wallCollision);
}
