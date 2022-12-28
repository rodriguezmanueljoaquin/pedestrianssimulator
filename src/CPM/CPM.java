package CPM;

import Agent.Agent;
import Environment.Environment;

import java.util.List;

public class CPM {
    private final double MAX_RADIUS = 1;
    private final double MIN_RADIUS = 1;
    private final double EXPANSION_TIME = 0.5;

    public static void updateAgents(List<Agent> agents, Environment environment) {
        // USAR CPM.CPM VIEJO!!!!


        // encontrar colisiones
//        List<Agent.Agent> wallCollisions = new ArrayList<>();
//        List<Vector<Agent.Agent>> agentsCollisions = new ArrayList<>();
//        for (int i = 0 ; i < agents.size() ; i++) {
//            // check wall collisions for agent i
//
//            for (int j = i+1 ; j < agents.size() ; j++) {
//                // check agent collision
//
//            }
//        }


        // actualizar velocidades de particulas que colisionaron con CPM.CPM
        // actualizar velocidades de particulas que no colisionaron con CPM.CPM
    }

//    private void radiusUpdate(Agent.Agent agent, boolean contact, Double DELTA_T) {
//        if (contact) {
//            agent.setRadius(this.MIN_RADIUS);
//        } else if (agent.getRadius() < this.MAX_RADIUS)
//            agent.setRadius(agent.getRadius() + MAX_RADIUS / (EXPANSION_TIME / DELTA_T));
//    }
}
