package InputHandling.SimulationParameters.AuxiliarClasses;

import Utils.Random.RandomInterface;

public class ServerGroupParameters {
    private final RandomInterface attendingDistribution;
    private final Integer maxCapacity;
    private final Double startTime;

    public ServerGroupParameters(RandomInterface attendingDistribution, Integer maxCapacity, Double startTime) {
        this.attendingDistribution = attendingDistribution;
        this.maxCapacity = maxCapacity;
        this.startTime = startTime;
    }

    public RandomInterface getAttendingDistribution() {
        return attendingDistribution;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Double getStartTime() {
        return startTime;
    }
}
