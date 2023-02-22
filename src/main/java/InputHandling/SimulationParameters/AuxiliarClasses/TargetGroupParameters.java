package InputHandling.SimulationParameters.AuxiliarClasses;

import Utils.Random.RandomInterface;

public class TargetGroupParameters {
    private final RandomInterface attendingDistribution;

    public TargetGroupParameters(RandomInterface attendingDistribution) {
        this.attendingDistribution = attendingDistribution;
    }

    public RandomInterface getAttendingDistribution() {
        return attendingDistribution;
    }
}
