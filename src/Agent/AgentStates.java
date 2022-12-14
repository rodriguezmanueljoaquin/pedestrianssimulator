package Agent;

public enum AgentStates {
    MOVING {
        @Override
        public double getVelocity() {
            return AgentConstants.STANDARD_VELOCITY;
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
            return AgentConstants.STANDARD_VELOCITY;
        }
    },
    STARTING {
        @Override
        public double getVelocity() {
            return 0.0;
        }
    };

    public abstract double getVelocity();
}
