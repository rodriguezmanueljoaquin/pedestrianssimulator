package Utils;

import Agent.AgentConstants;

public class Constants {
    // considers radius
    // used to change state at the state machine, or intermediate node in agent path
    public static double MINIMUM_DISTANCE_TO_TARGET = AgentConstants.MIN_RADIUS /2;
    public static double DOUBLE_EPSILON = .000001;
    public static double SPACE_BETWEEN_AGENTS_IN_QUEUE = AgentConstants.MAX_RADIUS * 2.5; //separate the points by the size of an agent and 50% of its radius more
    public static double MAX_TIME = 400;
}
