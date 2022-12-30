package CPM;

import Agent.Agent;
import Environment.Environment;
import Utils.Vector;
import Agent.AgentStates;

import java.util.List;
import java.util.stream.Collectors;

public class CPM {
    private static final double MAX_RADIUS = 5;
    private static final double MIN_RADIUS = 1;
    private static final double EXPANSION_TIME = 0.5;
    private static final double MAX_SPEED = 3.0;
    private static final double agentAp = 500, agentBp = 0.5, wallAp = 400, wallBp =0.5, beta = .9, tau = .5;
    public static void updateAgents(List<Agent> agents, Environment environment) {
        List<Agent> movingAgents = agents.stream().filter(a -> a.getState() == AgentStates.MOVING).collect(Collectors.toList());
        for(Agent agent : movingAgents){
            updateSpeedForAgent(agent, agents,environment);
        }
    }


    private static void updateSpeedForAgent(Agent agent,List<Agent> agents, Environment environment){
        Vector resultantNc = calculateResultantNc(agent,agents,environment);
        Vector resultantVelocity = calculateResultantVelocity(resultantNc,agent.getVelocity());

        agent.setVelocity(resultantVelocity);
    }

    private static Vector calculateResultantNc(Agent agent,List<Agent> agents, Environment environment){
        Vector resultantNc = new Vector(0,0);
        for(Agent collisionAgent : agents){
            if(collisionAgent.getId() == agent.getId())
                continue;
            //Si esto se rompe, podemos hacer otra cosa que es armar un objetivo falso en base a la velocidad a donde va
            resultantNc.add(calculateRepulsionForce(agent.getPosition(),collisionAgent.getPosition(),agent.getVelocity(),agentAp,agentBp));
        }
        Vector closestWallPosition = environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
        Vector wallRepulsion = calculateRepulsionForce(agent.getPosition(),closestWallPosition,agent.getVelocity(),wallAp,wallBp);
        return resultantNc.add(wallRepulsion).normalize();
    }

    private static Vector calculateResultantVelocity(Vector resultantNc, Vector velocity){
        Vector eit = velocity;
        Vector eia = resultantNc.add(eit).normalize();
//        double mod =  MAX_SPEED * Math.pow((agent.getRadius() - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS), beta);
//        if(agent.getRadius() < MAX_RADIUS)
//            agent.setRadius(agent.getRadius() + MAX_RADIUS*EXPANSION_TIME/tau);
        return eia.normalize();
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
