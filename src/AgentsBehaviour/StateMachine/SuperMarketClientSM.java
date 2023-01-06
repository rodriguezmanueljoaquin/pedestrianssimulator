package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentStates;
import Agent.AgentConstants;
import Environment.Objectives.ObjectiveType;
import GraphGenerator.Graph;

public class SuperMarketClientSM extends DefaultSM{
    public SuperMarketClientSM(Graph graph) {
        super(graph);
    }

    /* BEHAVIOUR OF A SUPERMARKET CLIENT AGENT:
        Same as DEFAULT but:
            when close to a target (which is considered as a product) reduces its velocity to simulate a "searching" for an specific product on the aile situation
    */

    @Override
    public void movingBehaviour(Agent agent, double currentTime) {
        if(agent.getCurrentObjective().getType().equals(ObjectiveType.TARGET)) {
            // only approximates slowly to targets
            if(agent.getPosition().distance(agent.getCurrentObjective().getPosition(agent)) < AgentConstants.MINIMUM_DISTANCE_TO_APPROXIMATING) {
                agent.setState(AgentStates.APPROXIMATING);
            }
        } else {
            super.movingBehaviour(agent, currentTime);
        }
    }

    @Override
    public void approximatingBehaviour(Agent agent, double currentTime) {
        // moving as super, just modifies its velocity through the state
        super.movingBehaviour(agent, currentTime);
    }
}
