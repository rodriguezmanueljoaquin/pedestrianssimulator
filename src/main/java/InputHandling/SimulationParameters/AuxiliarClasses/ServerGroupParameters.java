package InputHandling.SimulationParameters.AuxiliarClasses;

import Environment.Objectives.Server.QueueLine;
import Utils.Vector;

import java.util.HashMap;
import java.util.Map;

public class ServerGroupParameters {
    private final Double attendingTime;
    private final Integer maxCapacity;
    private final Double startTime; // FIXME: Deberia ser indicado por cada servidor no? No por el grupo
    private final Map<String, QueueLine> queues;

    public ServerGroupParameters(Double attendingTime, Integer maxCapacity, Double startTime) {
        this.attendingTime = attendingTime;
        this.maxCapacity = maxCapacity;
        this.startTime = startTime;
        this.queues = new HashMap<>();
    }

    public void addQueue(String queueId, Vector start, Vector end) {
        this.queues.put(queueId, new QueueLine(start, end));
    }

    public boolean hasQueue() {
        return this.queues.size() > 0;
    }

    public QueueLine getQueue(String queueId) {
        QueueLine queueLine = this.queues.get(queueId);
        if (queueLine == null)
            throw new RuntimeException("Queue with queueId: '" + queueId + "' not found");

        return this.queues.get(queueId);
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
