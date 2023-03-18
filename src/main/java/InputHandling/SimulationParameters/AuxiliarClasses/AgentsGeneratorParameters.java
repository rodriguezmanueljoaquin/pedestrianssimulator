package InputHandling.SimulationParameters.AuxiliarClasses;

import Utils.Random.RandomGenerator;

public class AgentsGeneratorParameters {
    private final double activeTime;
    private final double inactiveTime;
    private final String behaviourSchemeKey;
    private final AgentsParameters agentsParameters;
    private final GenerationParameters generationParameters;

    public AgentsGeneratorParameters(double activeTime, double inactiveTime, String behaviourSchemeKey,
                                     RandomGenerator minRadiusGenerator, RandomGenerator maxRadiusGenerator, double maxVelocity,
                                     double timeBetweenGenerations, RandomGenerator generationDistribution) {
        this.activeTime = activeTime;
        this.inactiveTime = inactiveTime;
        this.behaviourSchemeKey = behaviourSchemeKey;
        this.agentsParameters = new AgentsParameters(minRadiusGenerator, maxRadiusGenerator, maxVelocity);
        this.generationParameters = new GenerationParameters(timeBetweenGenerations, generationDistribution);
    }

    public double getActiveTime() {
        return this.activeTime;
    }

    public double getInactiveTime() {
        return this.inactiveTime;
    }

    public String getBehaviourSchemeKey() {
        return this.behaviourSchemeKey;
    }

    public GenerationParameters getGenerationParameters() {
        return this.generationParameters;
    }

    public AgentsParameters getAgentsParameters() {
        return this.agentsParameters;
    }

    public class AgentsParameters {
        private final RandomGenerator minRadiusGenerator, maxRadiusGenerator;
        private final double maxVelocity;

        public AgentsParameters(RandomGenerator minRadiusGenerator, RandomGenerator maxRadiusGenerator, double maxVelocity) {
            this.minRadiusGenerator = minRadiusGenerator;
            this.maxRadiusGenerator = maxRadiusGenerator;
            this.maxVelocity = maxVelocity;
        }

        public RandomGenerator getMaxRadiusGenerator() {
            return this.maxRadiusGenerator;
        }

        public RandomGenerator getMinRadiusGenerator() {
            return this.minRadiusGenerator;
        }

        public double getMaxVelocity() {
            return this.maxVelocity;
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
