package AgentsGenerator;

import Agent.Agent;
import AgentsBehaviour.BehaviourScheme;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import Utils.Random.RandomGenerator;
import Utils.Random.UniformRandom;
import Utils.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentsGenerator {
    private final String groupId;
    private final AgentsGeneratorZone zone;
    private final AgentsGeneratorParameters generatorParameters;
    private final BehaviourScheme behaviourScheme;
    private final RandomGenerator generationUnitsGenerator, generationIndexGenerator, minRadiusGenerator, maxRadiusGenerator;
    private final double agentsMaximumMostPossibleRadius;
    private double lastGenerationTime;

    public AgentsGenerator(String groupId, AgentsGeneratorZone zone, AgentsGeneratorParameters generatorParameters,
                           BehaviourScheme behaviourScheme, double agentsMaximumMostPossibleRadius, long seed) {
        this.groupId = groupId;
        this.zone = zone;
        this.generatorParameters = generatorParameters;
        this.generationUnitsGenerator = generatorParameters.getGenerationParameters().getGenerationUnitsGenerator();
        this.generationIndexGenerator = new UniformRandom(seed, 0, this.zone.getZoneMatrixSize()-1);
        this.minRadiusGenerator = generatorParameters.getAgentsParameters().getMinRadiusGenerator();
        this.maxRadiusGenerator = generatorParameters.getAgentsParameters().getMaxRadiusGenerator();
        this.agentsMaximumMostPossibleRadius = agentsMaximumMostPossibleRadius;
        this.behaviourScheme = behaviourScheme;
    }

    public List<Agent> generate(double time, List<Agent> currentAgents) {
        List<Agent> newAgents = new ArrayList<>();

        if (time % (this.generatorParameters.getActiveTime() + this.generatorParameters.getInactiveTime()) < this.generatorParameters.getActiveTime()
                && time - this.lastGenerationTime > this.generatorParameters.getGenerationParameters().getTimeBetweenGenerations()) {
            this.lastGenerationTime = time;
            // generate is on Active time, and it's time to create another generation
            int agentsToCreate = (int) Math.round(this.generationUnitsGenerator.getNewRandomNumber());
            if(agentsToCreate > this.zone.getZoneMatrixSize())
                agentsToCreate = this.zone.getZoneMatrixSize();

            Set<Integer> positionsUsed = new HashSet<>();
            // first, check positionsUsed by already existing agents
            for (Agent agent : currentAgents) {
                if(this.zone.isPointInside(agent.getPosition())) {
                    positionsUsed.add(this.zone.getIndexByPosition(agent.getPosition()));
                } else {
                    Vector closestPoint = agent.getPosition().add(
                            (this.zone.getMiddlePoint().subtract(agent.getPosition()))
                                    .scalarMultiply(agent.distance(this.zone.getMiddlePoint()) * this.agentsMaximumMostPossibleRadius)
                    );
                    if (this.zone.isPointInside(closestPoint)) {
                        positionsUsed.add(this.zone.getIndexByPosition(closestPoint));
                    }
                }
            }

            for (int i = 0; i < agentsToCreate; i++) {
                if(positionsUsed.size() == this.zone.getZoneMatrixSize())
                    break;

                // get position to generate agent, which has to be different from the ones the other agents get to avoid overlap
                int positionIndex;
                do {
                    positionIndex = (int) Math.round(this.generationIndexGenerator.getNewRandomNumber());
                } while (positionsUsed.contains(positionIndex));
                positionsUsed.add(positionIndex);

                AgentsGeneratorParameters.AgentsParameters agentsParameters = generatorParameters.getAgentsParameters();
                newAgents.add(
                        new Agent(
                                this.zone.getPositionByIndex(positionIndex),
                                this.minRadiusGenerator.getNewRandomNumber(),
                                this.maxRadiusGenerator.getNewRandomNumber(),
                                agentsParameters.getMaxVelocity(),
                                this.behaviourScheme.getStateMachine(),
                                this.behaviourScheme.getObjectivesSample()
                        )
                );
            }
        }
        return newAgents;
    }
}
