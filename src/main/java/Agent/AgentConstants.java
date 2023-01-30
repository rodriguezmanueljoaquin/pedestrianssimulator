package Agent;

public class AgentConstants {
    public static double MAX_RADIUS = .60;
    public static double MIN_RADIUS = .3;
    public static double B = .9; // used to calculate velocity dynamically according to current agent radius
    public static double MAXIMUM_VELOCITY = 1;
    // considers radius
    // used to change state at the state machine, or intermediate node in agent path
    public static double MINIMUM_DISTANCE_TO_TARGET = MIN_RADIUS;


    // For SuperMarketClient behaviour:
    public static double APPROXIMATING_VELOCITY = MAXIMUM_VELOCITY / 5;
    public static double MINIMUM_DISTANCE_TO_APPROXIMATING = 2;
}
