package OperationalModelModule;

public class CPMConstants {
    public static final double DTS_NEEDED_FOR_EXPANSION = 200;
    public static final double NEIGHBOURS_RADIUS = 3.0;
    public static final double TAU = 0.5; // time a particle needs to reach its maximum radius, from the minimum
    public static final double
            ORIGINAL_DIRECTION_AP = 700,
            NEW_DIRECTION_AP = 300,
            AGENT_AP = 250,
            AGENT_BP = 0.8,
            CLOSEST_WALL_AP = 150,
            CLOSEST_WALL_BP = 0.8, // cuanto mas grande, a mayor empieza a reaccionar
            CLOSEST_WALLS_AP = 150,
            CLOSEST_WALLS_BP = 0.8,
            AP_VARIATION = 50,
            BP_VARIATION = 0.1,
            NON_MOVING_AGENT_REPULSION_MULTIPLIER = 1,
            WALL_DISTANCE_CONSIDERATION = 3;
}
