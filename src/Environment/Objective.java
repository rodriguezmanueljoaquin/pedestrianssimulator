package Environment;

import Utils.Vector;

public interface Objective {
    public Vector getPosition();
    public Double getAttendingTime();
    public Boolean hasAttendingTime();
}
