package InputHandling.SimulationParameters.AuxiliarClasses;

import Utils.Random.RandomGenerator;

public class ServerGroupParameters {
    private final RandomGenerator attendingTimeGenerator;
    private final Integer maxCapacity;
    private final Double startTime;

    public ServerGroupParameters(RandomGenerator attendingTimeGenerator, Integer maxCapacity, Double startTime) {
        this.attendingTimeGenerator = attendingTimeGenerator;
        this.maxCapacity = maxCapacity;
        this.startTime = startTime;
    }

    public RandomGenerator getAttendingTimeGenerator() {
        return attendingTimeGenerator;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Double getStartTime() {
        return startTime;
    }
}
