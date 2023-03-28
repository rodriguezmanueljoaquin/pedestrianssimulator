package Agent;

public class AgentConstants {
    public static double B = .9; // used to calculate velocity dynamically according to current agent radius
    public static double MAXIMUM_VELOCITY_FACTOR = 1;
    // used to change state at the state machine, or intermediate node in agent path
    public static double MINIMUM_DISTANCE_TO_TARGET = 0.1;

    // For SuperMarketClient behaviour:
    public static double APPROXIMATING_VELOCITY_FACTOR = MAXIMUM_VELOCITY_FACTOR / 3;
    public static double MINIMUM_DISTANCE_TO_APPROXIMATING = 2;
}
