import java.util.Vector;

public class Agent {
    private Vector<Double> position;
    private Vector<Double> velocity;
    private static Integer count = 1;
    private final Integer id;
    private double radius;
    AgentStates state;
    // clock?

    public Agent(Vector<Double> x, Vector<Double> velocity, double radius, AgentStates state) {
        this.position = x;
        this.velocity = velocity;
        this.radius = radius;
        this.state = state;
        this.id = count++;
    }

    public void updatePosition(double time) {
        double newX = this.position.get(0) + time * this.velocity.get(0);
        double newY = this.position.get(1) + time * this.velocity.get(1);
        this.position.set(0, newX);
        this.position.set(1, newY);
    }

    public Integer getId() {
        return this.id;
    }

    public double getRadius(){
        return radius;
    }

    public void setRadius(double radius){
        this.radius = radius;
    }

    public Vector<Double> getPosition() {
        return this.position;
    }

    public void setPosition(Vector<Double> position){
        this.position = position;
    }

    public Vector<Double> getVelocity(){
        return this.velocity;
    }

    public void setVelocity(Vector<Double> speed){
        this.velocity = speed;
    }

    public AgentStates getState() {
        return state;
    }

    public void setState(AgentStates state) {
        this.state = state;
    }


    public double distance(Agent other) {
        double distanceX = Math.abs(this.getPosition().get(0) - other.getPosition().get(0));
        double distanceY = Math.abs(this.getPosition().get(1) - other.getPosition().get(1));

        return Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2)) - this.radius - other.radius;
    }
}

