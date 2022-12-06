package Agent;

import GraphGenerator.Graph;
import Utils.Constants;



public enum AgentStates {
    MOVING {
        @Override
        public double getVelocity() {
            return Constants.STANDARD_VELOCITY;
        }
    },
    ATTENDING {
        @Override
        public double getVelocity() {
            return 0.0;
        }
    },
    WAITING {
        @Override
        public double getVelocity() {
            return 0.0;
        }
    },
    LEAVING {
        //TODO: Get nearest exit.
        @Override
        public double getVelocity() {
            return Constants.STANDARD_VELOCITY;
        }
    },
    STARTING{
        @Override
        public double getVelocity() {
            return 0.0;
        }
    };
    public abstract double getVelocity();
}
