import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CPM {
    private final double MAX_RADIUS = 1;
    private final double MIN_RADIUS = 1;
    private final double EXPANSION_TIME = 0.5;

    public static void updateAgents(List<Agent> agents, Environment environment) {
        // encontrar colisiones
        List<Agent> wallCollisions = new ArrayList<>();
        List<Vector<Agent>> agentsCollisions = new ArrayList<>();
        for (int i = 0 ; i < agents.size() ; i++) {
            // check wall collisions for agent i

            for (int j = i+1 ; j < agents.size() ; j++) {
                // check agent collision

            }
        }



        // actualizar velocidades de particulas que colisionaron con CPM
        // actualizar velocidades de particulas que no colisionaron con CPM
    }

    private void radiusUpdate(Agent agent, boolean contact, Double DELTA_T) {
        if (contact) {
            agent.setRadius(this.MIN_RADIUS);
        } else if (agent.getRadius() < this.MAX_RADIUS)
            agent.setRadius(agent.getRadius() + MAX_RADIUS / (EXPANSION_TIME / DELTA_T));
    }

}
