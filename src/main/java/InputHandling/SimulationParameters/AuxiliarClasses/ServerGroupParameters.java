package InputHandling.SimulationParameters.AuxiliarClasses;

public class ServerGroupParameters {
    private final Double attendingTime;
    private final Integer maxCapacity;
    private final Double startTime;

    public ServerGroupParameters(Double attendingTime, Integer maxCapacity, Double startTime) {
        this.attendingTime = attendingTime;
        this.maxCapacity = maxCapacity;
        this.startTime = startTime;
    }

    public Double getAttendingTime() {
        return attendingTime;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Double getStartTime() {
        return startTime;
    }
}
