package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import Utils.Vector;

import java.util.List;

public class CPM {
    private static final double MAX_RADIUS = 5;
    private static final double MIN_RADIUS = 1;
    private static final double EXPANSION_TIME = 0.5;
    private static final double MAX_SPEED = 3.0;
    private static final double agentAp = 2000, agentBp = 0.2, wallAp = 500, wallBp =0.4, beta = .9, tau = .5;

    public static void updateAgent(Agent agent, List<Agent> agents, Environment environment){
        Vector resultantNc = calculateResultantNc(agent,agents,environment);
        Vector resultantVelocity = calculateResultantVelocity(resultantNc,agent.getVelocity());
//        System.out.println("Entered with velocity " + agent.getVelocity() + " and left with velocity: " + resultantVelocity);
        agent.setVelocity(resultantVelocity);
    }

    private static Vector calculateResultantNc(Agent agent,List<Agent> agents, Environment environment){
        Vector resultantNc = new Vector(0,0);
        for(Agent collisionAgent : agents){
            if(collisionAgent.getId() == agent.getId() || agent.getPosition().distance(collisionAgent.getPosition()) > 3.0)
                continue;
            resultantNc.add(calculateRepulsionForce(agent.getPosition(),collisionAgent.getPosition(),agent.getVelocity(),agentAp,agentBp));
        }
//        Vector closestWallPosition = environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
//        Vector wallRepulsion = calculateRepulsionForce(agent.getPosition(),closestWallPosition,agent.getVelocity(),wallAp,wallBp);
        return resultantNc;
    }

    private static Vector calculateResultantVelocity(Vector resultantNc, Vector velocity){
        Vector eit = velocity;
        Vector eia = resultantNc.add(eit).normalize();
//        double mod =  MAX_SPEED * Math.pow((agent.getRadius() - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS), beta);
//        if(agent.getRadius() < MAX_RADIUS)
//            agent.setRadius(agent.getRadius() + MAX_RADIUS*EXPANSION_TIME/tau);
        return eia;
    }


    private static Vector calculateRepulsionForce(Vector position,Vector obstacle, Vector objective, double Ap, double Bp){
        //eij (e sub ij)
        Vector repulsionVector = obstacle.substract(position).normalize();

        //dij (d sub ij) distance between position and otherPosition
        Double repulsionDistance = obstacle.distance(position);

//        Vector objectiveVector = objective.substract(position).normalize();
        Vector objectiveVector = objective.normalize();
        //cos(0) = a.b / |a||b| (ya estan normalizados o sea |a| = |b| = 1)
        double cosineOfTheta = objectiveVector.dotMultiply(obstacle);

        //eij*Ap*e^(-dij/bp)*cos(0)
        return repulsionVector.scalarMultiply(-Math.abs(Ap*Math.exp(-repulsionDistance/Bp)*cosineOfTheta));
    }

}
