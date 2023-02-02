package InputHandling.SimulationParameters.AuxiliarClasses;

public class AgentsGeneratorParameters {
    private final double activeTime;
    private final double inactiveTime;
    private final AgentsParameters agentsParameters;
    private final GenerationParameters generationParameters;

    public AgentsGeneratorParameters(double activeTime, double inactiveTime, double minRadius, double maxRadius,
                                     double maxVelocity, double timeBetweenGenerations, int minGeneration, int maxGeneration) {
        this.activeTime = activeTime;
        this.inactiveTime = inactiveTime;
        this.agentsParameters = new AgentsParameters(minRadius, maxRadius, maxVelocity);
        this.generationParameters = new GenerationParameters(timeBetweenGenerations, minGeneration, maxGeneration);
    }

    public double getActiveTime() {
        return activeTime;
    }

    public double getInactiveTime() {
        return inactiveTime;
    }

    public GenerationParameters getGenerationParameters() {
        return generationParameters;
    }

    public AgentsParameters getAgentsParameters() {
        return agentsParameters;
    }

    public class AgentsParameters {
        private final double maxRadius;
        private final double minRadius;
        private final double maxVelocity;

        public AgentsParameters(double minRadius, double maxRadius, double maxVelocity) {
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
            this.maxVelocity = maxVelocity;
        }

        public double getMaxRadius() {
            return maxRadius;
        }

        public double getMinRadius() {
            return minRadius;
        }

        public double getMaxVelocity() {
            return maxVelocity;
        }
    }

    public class GenerationParameters {
        private final double timeBetweenGenerations;
        private int minGeneration, maxGeneration;

        public GenerationParameters(double timeBetweenGenerations, int minGeneration, int maxGeneration) {
            if (minGeneration > maxGeneration || minGeneration < 0)
                throw new IllegalArgumentException("Bad arguments on generator agent creation limits");

            this.timeBetweenGenerations = timeBetweenGenerations;
            this.minGeneration = minGeneration;
            this.maxGeneration = maxGeneration;
        }

        public double getTimeBetweenGenerations() {
            return timeBetweenGenerations;
        }

        public int getMinGeneration() {
            return minGeneration;
        }

        public int getMaxGeneration() {
            return maxGeneration;
        }
    }
}
