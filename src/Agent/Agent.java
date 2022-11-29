package Agent;

import Utils.Vector;

public class Agent {
    private Vector position;
    private Vector velocity;
    private static Integer count = 1;
    private final Integer id;
    private double radius;
    AgentStates state;
    // clock?

    public Agent(Vector x, Vector velocity, double radius, AgentStates state) {
        this.position = x;
        this.velocity = velocity;
        this.radius = radius;
        this.state = state;
        this.id = count++;
    }

    public void updatePosition(double time) {
        this.position = this.position.add(this.velocity.scalarMultiply(time));
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

    public Vector getPosition() {
        return this.position;
    }

    public void setPosition(Vector position){
        this.position = position;
    }

    public Vector getVelocity(){
        return this.velocity;
    }

    public void setVelocity(Vector speed){
        this.velocity = speed;
    }

    public AgentStates getState() {
        return state;
    }

    public void setState(AgentStates state) {
        this.state = state;
    }


    public double distance(Agent other) {
        return this.getPosition().distance(other.position) - this.radius - other.radius;
    }
}

