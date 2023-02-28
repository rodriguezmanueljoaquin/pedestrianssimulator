package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;

import java.util.List;
import java.util.Random;

public class CPMAnisotropic implements OperationalModelModule{

    @Override
    public void updateAgents(List<Agent> agents) {
        //Agents should only be directed to colliding agents
        //if the crash is in the front
        //Wall collisions should only be considered if distance
        //is less than r min (weird since r min implies a collision)
        //ask mr. rafa

        //Colliding conditions with agents greatly complicates
        //if agent.getRadius() == r min and agent.distance(otherAgent) < 0
            //collisio
        //if agent.getRadius() != r min and isInFieldOfView &&
        //lines parallel to agent direction from both borders of r min
        //any of those intersects with the current radius of agent
        //any of those intersections must be less than radius
    }

    @Override
    public void updateCollidingAgents(AgentsCollision agentsCollision) {
        //COLLIDING AGENTS / AGENTS with r min should behave
        //EXACTLY LIKE CPM

    }

    @Override
    public void updateWallCollidingAgent(WallCollision wallCollision) {

    }

    @Override
    public void expandAgent(Agent agent) {

    }

    @Override
    public void updateNonCollisionAgent(Agent agent, double dt, Random random) {

    }
}
