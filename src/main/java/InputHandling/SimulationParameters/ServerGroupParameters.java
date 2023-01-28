package InputHandling.SimulationParameters;

public class ServerGroupParameters {
    private final Double attendingTime;
    private final Integer maxCapacity;
    private final Boolean hasQueue;
    private final Double startTime; // FIXME: Deberia ser indicado por cada servidor no? No por el grupo

    public ServerGroupParameters(Double attendingTime, Integer maxCapacity, Boolean hasQueue, Double startTime) {
        this.attendingTime = attendingTime;
        this.maxCapacity = maxCapacity;
        this.hasQueue = hasQueue;
        this.startTime = startTime;
    }

    public Double getAttendingTime() {
        return attendingTime;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Boolean hasQueue() {
        return hasQueue;
    }

    public Double getStartTime() {
        return startTime;
    }
}
