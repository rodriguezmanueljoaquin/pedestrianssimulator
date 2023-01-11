package OperationalModelModule.Collisions;

import Agent.Agent;

import java.util.Objects;

public class AgentsCollision {
    private final Agent agent1, agent2;

    public AgentsCollision(Agent agent1, Agent agent2) {
        this.agent1 = agent1;
        this.agent2 = agent2;
    }

    public Agent getAgent1() {
        return agent1;
    }

    public Agent getAgent2() {
        return agent2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentsCollision that = (AgentsCollision) o;
        return Objects.equals(agent1, that.agent1) && Objects.equals(agent2, that.agent2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent1, agent2);
    }
}
