package Environment;

import Utils.Vector;

public abstract class Objective {
    public abstract Vector getPosition();
    public abstract Double getAttendingTime();
    public abstract Boolean hasAttendingTime();
}
