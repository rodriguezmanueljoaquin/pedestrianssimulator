package Agent;

public class AgentConstants {
    public static double MAX_RADIUS_OF_ALL_AGENTS = .70; // TODO: SHOULD PICK THE MAX RADIUS OF THOSE SPECIFIED IN THE GENERATORSPARAMATERS
    public static double B = .9; // used to calculate velocity dynamically according to current agent radius
    public static double MAXIMUM_VELOCITY = 1;
    // used to change state at the state machine, or intermediate node in agent path
    public static double MINIMUM_DISTANCE_TO_TARGET = 0.2;

    // For SuperMarketClient behaviour:
    public static double APPROXIMATING_VELOCITY = MAXIMUM_VELOCITY / 3;
    public static double MINIMUM_DISTANCE_TO_APPROXIMATING = 2;
}
