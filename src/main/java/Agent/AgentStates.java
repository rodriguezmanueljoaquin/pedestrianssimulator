package Agent;

public enum AgentStates {
    MOVING {
        @Override
        public double getMaxVelocityFactor() {
            return AgentConstants.MAXIMUM_VELOCITY_FACTOR;
        }
    },
    MOVING_TO_QUEUE_POSITION {
        @Override
        public double getMaxVelocityFactor() {
            return AgentConstants.MAXIMUM_VELOCITY_FACTOR;
        }
    },
    ATTENDING {
        @Override
        public double getMaxVelocityFactor() {
            return 0.0;
        }
    },
    WAITING_IN_QUEUE {
        @Override
        public double getMaxVelocityFactor() {
            return 0.0;
        }
    },
    LEAVING {
        @Override
        public double getMaxVelocityFactor() {
            return AgentConstants.MAXIMUM_VELOCITY_FACTOR;
        }
    },
    LEFT {
        @Override
        public double getMaxVelocityFactor() {
            return 0.0;
        }
    },
    STARTING {
        @Override
        public double getMaxVelocityFactor() {
            return 0.0;
        }
    },

    /* for SuperMarketClientSM */
    APPROXIMATING {
        @Override
        public double getMaxVelocityFactor() {
            return AgentConstants.APPROXIMATING_VELOCITY_FACTOR;
        }
    };

    public abstract double getMaxVelocityFactor();
}
