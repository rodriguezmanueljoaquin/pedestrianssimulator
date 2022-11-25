import java.util.Vector;

public class Server {
    private final Vector<Double> position;
    private final int maxAttendants;
    private int currentAttendants;
    private static Integer count = 1;
    private final Integer id;

    public Server(Vector<Double> position, int maxCapacity) {
        this.position = position;
        this.maxAttendants = maxCapacity;
        this.currentAttendants = 0;
        this.id = count++;
    }

    public boolean hasCapacity() {
        return this.currentAttendants < this.maxAttendants;
    }

    public void increaseCapacity() {
        this.currentAttendants++;
    }

    public Vector<Double> getPosition() {
        return this.position;
    }

    public Integer getId() {
        return this.id;
    }
}
