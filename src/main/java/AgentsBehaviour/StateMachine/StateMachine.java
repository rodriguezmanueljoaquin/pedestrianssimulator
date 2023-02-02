package AgentsBehaviour.StateMachine;

import Agent.Agent;

public interface StateMachine {
    void updateAgentCurrentPath(Agent agent);

    void updateAgent(Agent agent, double currentTime);

    // States which transition behaviour can be modified from Default state machine:
    void movingBehaviour(Agent agent, double currentTime);

    void approximatingBehaviour(Agent agent, double currentTime);
}
