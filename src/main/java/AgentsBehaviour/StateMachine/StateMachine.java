package AgentsBehaviour.StateMachine;

import Agent.Agent;
import Environment.Objectives.Exit;

import java.util.List;

public interface StateMachine {

    void updateAgent(Agent agent, double currentTime);

    // States which transition behaviour can be modified from Default state machine:
    void movingBehaviour(Agent agent, double currentTime);

    void approximatingBehaviour(Agent agent, double currentTime);

    void evacuate(Agent agent, List<Exit> exits);
}
