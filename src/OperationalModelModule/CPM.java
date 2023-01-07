package OperationalModelModule;

import Agent.Agent;
import Environment.Environment;
import Utils.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class CPM {
    private static final double MAX_RADIUS = 5;
    private static final double MIN_RADIUS = 1;
    private static final double EXPANSION_TIME = 0.5;
    private static final double MAX_SPEED = 3.0;
    private static final double NEIGHBOURS_RADIUS = 3.0;
    private static final double agentAp = 2000, agentBp = 0.2, wallAp = 500, wallBp = 0.4, beta = .9, tau = .5;
    // TODO: MAPA<ID DE AGENTE, CPMAGENT> PARA ASOCIAR COEFICIENTES DISTINTOS (randomizados un poco desde un valor) A LOS AGENTS

    public static void updateAgent(Agent agent, List<Agent> agents, Environment environment) {
        Vector heuristicVelocity = calculateHeuristicVelocity(agent, agents, environment);
        Vector resultantDirection =
                calculateTargetDirection(heuristicVelocity, agent.getVelocity()); // takes in account both heuristic velocity and original agent velocity
//        System.out.println("Entered with velocity " + agent.getVelocity() + " and left with velocity: " + resultantVelocity);
        agent.setVelocity(resultantDirection.scalarMultiply(agent.getState().getVelocity()));
    }

    private static Vector calculateHeuristicVelocity(Agent agent, List<Agent> agents, Environment environment) {
        Vector resultantNc = new Vector(0, 0);
        List<Agent> neighbours = agents.stream()
                .filter(other -> agent.getPosition().distance(other.getPosition()) > NEIGHBOURS_RADIUS && !agent.equals(other))
                .collect(Collectors.toList());

        for (Agent neighbour : neighbours) {
            resultantNc.add(calculateRepulsionForce(agent.getPosition(), neighbour.getPosition(), agent.getVelocity(), agentAp, agentBp));
        }
//        Vector closestWallPosition = environment.getClosestWall(agent.getPosition()).getClosestPoint(agent.getPosition());
//        Vector wallRepulsion = calculateRepulsionForce(agent.getPosition(),closestWallPosition,agent.getVelocity(),wallAp,wallBp);
        return resultantNc;
    }

    private static Vector calculateTargetDirection(Vector heuristicVelocity, Vector originalVelocity) {
        return heuristicVelocity.add(originalVelocity).normalize();
    }


    private static Vector calculateRepulsionForce(Vector position, Vector obstacle, Vector objective, double Ap, double Bp) {
        //eij (e sub ij)
        Vector repulsionVector = obstacle.substract(position).normalize();

        //dij (d sub ij) distance between position and otherPosition
        Double repulsionDistance = obstacle.distance(position);

//        Vector objectiveVector = objective.substract(position).normalize();
        Vector objectiveVector = objective.normalize();
        //cos(0) = a.b / |a||b| (ya estan normalizados o sea |a| = |b| = 1)
        double cosineOfTheta = objectiveVector.dotMultiply(obstacle);

        //eij*Ap*e^(-dij/bp)*cos(0)
        return repulsionVector.scalarMultiply(-Math.abs(Ap * Math.exp(-repulsionDistance / Bp) * cosineOfTheta));
    }

}
