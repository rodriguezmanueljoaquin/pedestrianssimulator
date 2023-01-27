package SimulationParameters;

public class ServerGroupParameters {
    private final Double attendingTime;
    private final Integer maxAttendants;

    public ServerGroupParameters(Double attendingTime, Integer maxAttendants) {
        this.attendingTime = attendingTime;
        this.maxAttendants = maxAttendants;
    }

    public Double getAttendingTime() {
        return attendingTime;
    }

    public Integer getMaxAttendants() {
        return maxAttendants;
    }
}
