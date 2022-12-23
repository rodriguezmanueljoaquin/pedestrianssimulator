package AgentsBehaviour.StateMachine;

import Agent.Agent;
import GraphGenerator.Graph;

public interface StateMachine {
    void updateAgent(Agent agent, double currentTime);
}
