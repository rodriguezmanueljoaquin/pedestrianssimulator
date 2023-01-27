package SimulationParameters;

public class AgentsParameters {
    private final double maxRadius,
                        minRadius,
                        maxVelocity;

    public AgentsParameters(double maxRadius, double minRadius, double maxVelocity) {
        this.maxRadius = maxRadius;
        this.minRadius = minRadius;
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
