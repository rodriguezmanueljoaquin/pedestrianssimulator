package Environment;

import java.util.Vector;

public class Target {
    private final Vector<Double> position;
    private static Integer count = 1;
    private final Integer id;
    private final Long attendingTime; //milliseconds needed to complete task

    // should have information about if it is occupied?

    public Target(Vector<Double> position, Long attendingTime) {
        this.position = position;
        this.attendingTime = attendingTime;
        this.id = count++;
    }

    public Vector<Double> getPosition() {
        return position;
    }

    public Long getAttendingTime() {
        return attendingTime;
    }
}
