package InputHandling.SimulationParameters.AuxiliarClasses;

import Utils.Random.RandomGenerator;

public class TargetGroupParameters {
    private final RandomGenerator attendingTimeGenerator;

    public TargetGroupParameters(RandomGenerator attendingTimeGenerator) {
        this.attendingTimeGenerator = attendingTimeGenerator;
    }

    public RandomGenerator getAttendingTimeGenerator() {
        return attendingTimeGenerator;
    }
}
