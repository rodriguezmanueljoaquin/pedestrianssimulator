package Agent;

import GraphGenerator.Graph;
import Utils.Constants;

public enum AgentStates {
    MOVING {
        @Override
        public AgentStates nextState(Agent agent, double currentTime) {
            if (agent.getPosition().distance(agent.getCurrentObjective().getPosition()) < Constants.MINIMUM_DISTANCE_TO_TAGRET) {
                if (agent.getCurrentObjective().hasAttendingTime()) {
                    agent.setStartedAttendingAt(currentTime);
                    return ATTENDING;
                } else return WAITING;
            }
            return MOVING;
        }

        @Override
        public double getVelocity() {
            return Constants.STANDARD_VELOCITY;
        }
    },
    ATTENDING {
        @Override
        public AgentStates nextState(Agent agent, double currentTime) {
            if (currentTime - agent.getStartedAttendingAt() < agent.getCurrentObjective().getAttendingTime())
                return ATTENDING;
            agent.getNextObjective();
            if (agent.hasObjectives())
                return MOVING;
            return LEAVING;
        }

        @Override
        public double getVelocity() {
            return 0.0;
        }
    },
    WAITING {
        //TODO IMPLEMENT WAITING: Server should say when to stop waiting.
        @Override
        public AgentStates nextState(Agent agent, double currentTime) {
            return null;
        }

        @Override
        public double getVelocity() {
            return 0.0;
        }
    },
    LEAVING {
        //TODO: Get nearest exit.
        @Override
        public AgentStates nextState(Agent agent, double currentTime) {
            return null;
        }

        @Override
        public double getVelocity() {
            return Constants.STANDARD_VELOCITY;
        }
    };


    private Graph graph;

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    public abstract AgentStates nextState(Agent agent, double currentTime);

    public abstract double getVelocity();
}
