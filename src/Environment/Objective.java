package Environment;

import Utils.Vector;

public interface Objective {
    Vector getPosition();

    Double getAttendingTime();

    Boolean hasAttendingTime();
}
