package Utils;

import Agent.AgentConstants;

public class Constants {
    public static double MINIMUM_DISTANCE_TO_TARGET = 0.05; // to change state at the state machine
    public static double DOUBLE_EPSILON = .000001;
    public static double SPACE_BETWEEN_AGENTS_IN_QUEUE = AgentConstants.MAX_RADIUS * 2.5; //separate the points by the size of an agent and 50% of its radius more
    public static double DELTA_T = 0.025;
    public static double FRAME_EXPORT_DELTA_T = 0.25;
    public static double MAX_TIME = 200;
}
