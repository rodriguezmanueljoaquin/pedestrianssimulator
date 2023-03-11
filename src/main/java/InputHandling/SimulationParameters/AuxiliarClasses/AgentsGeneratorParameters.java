package InputHandling.SimulationParameters.AuxiliarClasses;

import Utils.Random.RandomGenerator;

public class AgentsGeneratorParameters {
    private final double activeTime;
    private final double inactiveTime;
    private final String behaviourSchemeKey;
    private final AgentsParameters agentsParameters;
    private final GenerationParameters generationParameters;

    public AgentsGeneratorParameters(double activeTime, double inactiveTime, String behaviourSchemeKey, double minRadius,
                                     double maxRadius, double maxVelocity, double timeBetweenGenerations, RandomGenerator generationDistribution) {
        this.activeTime = activeTime;
        this.inactiveTime = inactiveTime;
        this.behaviourSchemeKey = behaviourSchemeKey;
        this.agentsParameters = new AgentsParameters(minRadius, maxRadius, maxVelocity);
        this.generationParameters = new GenerationParameters(timeBetweenGenerations, generationDistribution);
    }

    public double getActiveTime() {
        return activeTime;
    }

    public double getInactiveTime() {
        return inactiveTime;
    }

    public String getBehaviourSchemeKey() {
        return behaviourSchemeKey;
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
        private final RandomGenerator generationUnitsGenerator;

        public GenerationParameters(double timeBetweenGenerations, RandomGenerator generationUnitsGenerator) {
            this.timeBetweenGenerations = timeBetweenGenerations;
            this.generationUnitsGenerator = generationUnitsGenerator;
        }

        public double getTimeBetweenGenerations() {
            return timeBetweenGenerations;
        }

        public RandomGenerator getGenerationUnitsGenerator() {
            return generationUnitsGenerator;
        }
    }
}
