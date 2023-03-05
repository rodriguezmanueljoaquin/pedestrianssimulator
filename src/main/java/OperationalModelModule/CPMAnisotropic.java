package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import OperationalModelModule.Collisions.AgentsCollision;
import OperationalModelModule.Collisions.WallCollision;
import Utils.Vector;

import java.util.List;
import java.util.Random;

public class CPMAnisotropic implements OperationalModelModule{

    @Override
    public void updateAgents(List<Agent> agents) {
        //Agents should only be directed to colliding agents
        //if the crash is in the front
        //Wall collisions should only be considered if distance
        //is less than r min (weird since r min implies a collision)
        //ask mr. rafa

        //Colliding conditions with agents greatly complicates
        //if agent.getRadius() == r min and agent.distance(otherAgent) < 0
            //collision
        //if agent.getRadius() != r min and isInFieldOfView &&
        //lines parallel to agent direction from both borders of r min
        //any of those intersects with the current radius of agent
        //any of those intersections must be less than radius
    }

    @Override
    public void updateCollidingAgents(AgentsCollision agentsCollision) {
        //COLLIDING AGENTS / AGENTS with r min should behave
        //EXACTLY LIKE CPM

    }

    @Override
    public void updateWallCollidingAgent(WallCollision wallCollision) {

    }

    @Override
    public void expandAgent(Agent agent) {

    }

    @Override
    public void updateNonCollisionAgent(Agent agent, double dt, Random random) {

    }

    private static double getBeta(Agent agent, Agent otherAgent) {
        //As PDF says, Beta = the angle formed by speed of agent and the relative direction between other agent and agent
        Vector relativeDirection = otherAgent.getPosition().substract(agent.getPosition());
        Vector agentDirection = agent.getDirection();
        // We use acos to calculate this direction
        return Math.acos(agentDirection.dotMultiply(relativeDirection)/(agentDirection.module()* relativeDirection.module());
    }

    public static boolean isInContact(Agent agent, Agent otherAgent) {
        if(agent.getRadius() == agent.getMinRadius()) {
            // like CPM
            return agent.distance(otherAgent) < 0;
        } else {
            double beta = getBeta(agent,otherAgent);
            return beta >= 0 && beta <= Math.PI / 2 &&
                    agent.distance(otherAgent) < agent.getRadius() &&
                    parallelLinesIntersectAgentRadiusAndDistanceIsValid(agent,otherAgent);
        }
    }

    public static boolean parallelLinesIntersectAgentRadiusAndDistanceIsValid(Agent agent, Agent otherAgent) {
//        https://math.stackexchange.com/questions/422602/convert-two-points-to-line-eq-ax-by-c-0
//        https://math.stackexchange.com/questions/1481904/distance-between-line-and-circle
//        Aca, en vez de calcular para las dos lineas paralelas me fijo si la distancia
//        Entre el circulo y la linea menos el radio del agente es menor que 0
//        Es distinto a lo que dice el paper asi que checkear con Rafa
//        Revisar esto y ver como hago para sacar el punto P que es el mas cercano, asi lo comparo con
//        la distancia al centro del agente y me fijo que sea menor a 2*ri que es la segunda condicion del Ademas
//        SI REVERTIMOS LAS CONDICIONES, LEER EL EXTRA, solo deberia llamar esta funcion si el radio es menor a 2*ri
        Vector point1 = agent.getPosition();
        Vector point2 = agent.getPosition().add(agent.getDirection());
        Vector circleCenter = otherAgent.getPosition();
        double circleRadius = otherAgent.getRadius();
        return distanceLineFromCircle(point1,point2,circleCenter) - agent.getRadius() - circleRadius< 0;
    }

    private static double distanceLineFromCircle(Vector point1, Vector point2, Vector circleCenter) {
        double A = point1.getY() - point2.getY();
        double B = point2.getX() - point1.getX();
        double C = point1.getX()*point2.getY() - point2.getX()*point1.getY();
        return Math.abs(A*circleCenter.getX() + B*circleCenter.getY() + C)/Math.sqrt(A*A + B*B);
    }
}
