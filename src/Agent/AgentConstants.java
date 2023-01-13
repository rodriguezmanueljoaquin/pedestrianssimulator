package Agent;

public class AgentConstants {
    public static double MAX_RADIUS = .50;
    public static double MIN_RADIUS = .25;
    public static double B = .9; // used to calculate velocity dynamically according to current agent radius
    public static double STANDARD_VELOCITY = 1.0;


    // For SuperMarketClient behaviour:
    public static double APPROXIMATING_VELOCITY = STANDARD_VELOCITY / 5;
    public static double MINIMUM_DISTANCE_TO_APPROXIMATING = 2;
}
