package Agent;

import Utils.Constants;

public enum AgentStates {
    MOVING {
        @Override
        public AgentStates nextState(Agent agent,double currentTime){
            if(agent.getPosition().distance(agent.getCurrentObjective().getPosition()) < Constants.MINIMUM_DISTANCE_TO_TAGRET){
                if(agent.getCurrentObjective().hasAttendingTime()) {
                    agent.setStartedAttendingAt(currentTime);
                    return ATTENDING;
                } else return WAITING;
            }
            return MOVING;
        }
    },
    ATTENDING {
        @Override
        public AgentStates nextState(Agent agent,double currentTime){
            if(currentTime - agent.getStartedAttendingAt() < agent.getCurrentObjective().getAttendingTime())
                return ATTENDING;
            agent.getNextObjective();
            if(agent.hasObjectives())
                return MOVING;
            return LEAVING;
        }
    },
    WAITING {
        //TODO IMPLEMENT WAITING: Server should say when to stop waiting.
        @Override
        public AgentStates nextState(Agent agent,double currentTime){
            return null;
        }
    },
    LEAVING {
        //TODO: Get nearest exit.
        @Override
        public AgentStates nextState(Agent agent,double currentTime){
            return null;
        }
    };

    public abstract AgentStates nextState(Agent agent,double currentTime);
}
