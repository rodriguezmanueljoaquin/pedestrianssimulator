package InputHandling.SimulationParameters;

public class AgentsGeneratorParameters {
    private final double activeTime;
    private final double inactiveTime;
    private final GenerationParameters generationParameters;

    public AgentsGeneratorParameters(double activeTime, double inactiveTime, double timeBetweenGenerations, int minGeneration, int maxGeneration) {
        this.activeTime = activeTime;
        this.inactiveTime = inactiveTime;
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

    public class GenerationParameters {
        private final double timeBetweenGenerations;
        private int minGeneration, maxGeneration;

        public GenerationParameters(double timeBetweenGenerations, int minGeneration, int maxGeneration) {
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
