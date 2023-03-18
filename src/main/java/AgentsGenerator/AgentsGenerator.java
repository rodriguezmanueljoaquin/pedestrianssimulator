package AgentsGenerator;

import Agent.Agent;
import AgentsBehaviour.BehaviourScheme;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import Utils.Random.RandomGenerator;
import Utils.Random.UniformRandom;

import java.util.ArrayList;
import java.util.List;

public class AgentsGenerator {
    private final String groupId;
    private final AgentsGeneratorZone zone;
    private final AgentsGeneratorParameters generatorParameters;
    private final BehaviourScheme behaviourScheme;
    private final RandomGenerator generationUnitsGenerator, generationCellGenerator, minRadiusGenerator, maxRadiusGenerator;
    private double lastGenerationTime;

    public AgentsGenerator(String groupId, AgentsGeneratorZone zone, AgentsGeneratorParameters generatorParameters,
                           BehaviourScheme behaviourScheme, long seed) {
        this.groupId = groupId;
        this.zone = zone;
        this.generatorParameters = generatorParameters;
        this.generationUnitsGenerator = generatorParameters.getGenerationParameters().getGenerationUnitsGenerator();
        this.generationCellGenerator = new UniformRandom(seed, 0, this.zone.getZoneMatrixSize());
        this.minRadiusGenerator = generatorParameters.getAgentsParameters().getMinRadiusGenerator();
        this.maxRadiusGenerator = generatorParameters.getAgentsParameters().getMaxRadiusGenerator();
        this.behaviourScheme = behaviourScheme;
    }

    public List<Agent> generate(double time) {
        List<Agent> agents = new ArrayList<>();

        if (time % (this.generatorParameters.getActiveTime() + this.generatorParameters.getInactiveTime()) < this.generatorParameters.getActiveTime()
                && time - this.lastGenerationTime > this.generatorParameters.getGenerationParameters().getTimeBetweenGenerations()) {
            this.lastGenerationTime = time;
            // generate is on Active time, and it's time to create another generation
            int agentsToCreate = (int) Math.round(this.generationUnitsGenerator.getNewRandomNumber());
            if(agentsToCreate > this.zone.getZoneMatrixSize())
                agentsToCreate = this.zone.getZoneMatrixSize();

            List<Integer> positionsUsed = new ArrayList<>();
            for (int i = 0; i < agentsToCreate; i++) {
                // get position to generate agent, which has to be different from the ones the other agents get to avoid overlap
                int positionIndex;
                do {
                    positionIndex = (int) Math.round(this.generationCellGenerator.getNewRandomNumber());
                } while (positionsUsed.contains(positionIndex));
                positionsUsed.add(positionIndex);

                AgentsGeneratorParameters.AgentsParameters agentsParameters = generatorParameters.getAgentsParameters();
                agents.add(
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
        return agents;
    }
}
