package OperationalModelModule;

public class CPMConstants {
    public static final double DTS_NEEDED_FOR_EXPANSION = 5;
    public static final double NEIGHBOURS_RADIUS = 3.0;
    public static final double
            ORIGINAL_DIRECTION_AP = 200,
            AGENT_AP = 150,
            AGENT_BP = 0.7,
            WALL_AP = 500,
            WALL_BP = 1, // cuanto mas grande, a mayor distancia reacciona más
            AP_VARIATION = 25,
            BP_VARIATION = 0.1,
            NON_MOVING_AGENT_REPULSION_MULTIPLIER = 3;
}
