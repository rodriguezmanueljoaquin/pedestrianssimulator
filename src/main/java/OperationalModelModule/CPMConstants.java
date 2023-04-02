package OperationalModelModule;

public class CPMConstants {
    public static final double DTS_NEEDED_FOR_EXPANSION = 200;
    public static final double NEIGHBORS_RADIUS = 3.0;
    public static final double TAU = 0.5; // time a particle needs to reach its maximum radius, from the minimum
    public static final double
            ORIGINAL_DIRECTION_AP = 400,
            NEW_DIRECTION_AP = 200,
            AGENT_AP = 250,
            AGENT_BP = 1,

    // ----------- WALLS COEFFICIENTS -----------
    // IF CONSIDERING ONLY THE CLOSEST WALL FOR HEURISTIC DIRECTION
            CLOSEST_WALL_AP = 250,
            CLOSEST_WALL_BP = 0.8, // cuanto mas grande, a mayor empieza a reaccionar
    // IF CONSIDERING ONLY ALL THE WALL IN THE WALL DISTANCE CONSIDERATION RADIUS FOR HEURISTIC DIRECTION
            CLOSEST_WALLS_AP = 150,
            CLOSEST_WALLS_BP = 0.8,
    // ----------- WALLS COEFFICIENTS -----------


            AP_VARIATION = 50,
            BP_VARIATION = 0.1,
            NON_MOVING_AGENT_REPULSION_MULTIPLIER = 1,
            WALL_DISTANCE_CONSIDERATION = 3;
}
