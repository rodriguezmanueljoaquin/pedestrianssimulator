package Agent;

public enum AgentStates {
    MOVING {
        @Override
        public double getVelocity() {
            return AgentConstants.MAXIMUM_VELOCITY;
        }
    },
    MOVING_TO_QUEUE_POSITION {
        @Override
        public double getVelocity() {
            return AgentConstants.MAXIMUM_VELOCITY;
        }
    },
    ATTENDING {
        @Override
        public double getVelocity() {
            return 0.0;
        }
    },
    WAITING_IN_QUEUE {
        @Override
        public double getVelocity() {
            return 0.0;
        }
    },
    LEAVING {
        //TODO: Get nearest exit.
        @Override
        public double getVelocity() {
            return AgentConstants.MAXIMUM_VELOCITY;
        }
    },
    STARTING {
        @Override
        public double getVelocity() {
            return 0.0;
        }
    },

    /* for SuperMarketClientSM */
    APPROXIMATING {
        @Override
        public double getVelocity() {
            return AgentConstants.APPROXIMATING_VELOCITY;
        }
    };

    public abstract double getVelocity();
}
