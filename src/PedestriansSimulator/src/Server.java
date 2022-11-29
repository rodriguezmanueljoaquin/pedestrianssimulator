import Utils.Vector;

public class Server {
    private final Vector position;
    private final int maxAttendants;
    private int currentAttendants;
    private static Integer count = 1;
    private final Integer id;

    public Server(Vector position, int maxCapacity) {
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

    public Vector getPosition() {
        return this.position;
    }

    public Integer getId() {
        return this.id;
    }
}
