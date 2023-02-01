package Utils;

import Agent.AgentConstants;

public class Constants {
    public static double DELTA_T = AgentConstants.MAX_RADIUS / (2 * AgentConstants.MAXIMUM_VELOCITY);
    public static double DOUBLE_EPSILON = .000001;
    public static double SPACE_BETWEEN_AGENTS_IN_QUEUE = AgentConstants.MAX_RADIUS * 2.5; //separate the points by the size of an agent and 50% of its radius more
    public static double MAX_TIME = 400;
    public static double LEAVING_TIME = 1.0;
}
