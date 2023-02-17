package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Agent.AgentConstants;
import Agent.AgentStates;
import Environment.Objectives.ObjectiveType;
import Environment.Objectives.Target.BorderTarget;
import Environment.Objectives.Target.Target;
import GraphGenerator.Graph;

public class SuperMarketClientSM extends DefaultSM {
    public SuperMarketClientSM(Graph graph) {
        super(graph);
    }

    /* BEHAVIOUR OF A SUPERMARKET CLIENT AGENT:
        Same as DEFAULT but:
            when close to a target (which is considered as a product) reduces its velocity to simulate a "searching" for an specific product on the aile situation
    */

    @Override
    public void movingBehaviour(Agent agent, double currentTime) {
        if (agent.getCurrentObjective().getType().equals(ObjectiveType.TARGET)) {
            Target target = (Target) agent.getCurrentObjective();
            if (agent.distance(target.getPosition(agent)) < AgentConstants.MINIMUM_DISTANCE_TO_APPROXIMATING) {
                agent.setState(AgentStates.APPROXIMATING);
            }
        } else {
            // agent only approximates when objective is a target
            super.movingBehaviour(agent, currentTime);
        }
    }

    @Override
    public void approximatingBehaviour(Agent agent, double currentTime) {
        // moving as super, just modifies its velocity through the state
        super.movingBehaviour(agent, currentTime);
    }
}
