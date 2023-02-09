package AgentsGenerator;

import Agent.Agent;
import AgentsBehaviour.BehaviourScheme;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgentsGenerator {
    private final String groupId;
    private final AgentsGeneratorZone zone;
    private final AgentsGeneratorParameters generatorParameters;
    private final BehaviourScheme behaviourScheme;
    private final Random random;
    private int minGeneration, maxGeneration;
    private double lastGenerationTime;

    public AgentsGenerator(String groupId, AgentsGeneratorZone zone, AgentsGeneratorParameters generatorParameters, BehaviourScheme behaviourScheme, long randomSeed) {
        this.groupId = groupId;
        this.zone = zone;
        this.generatorParameters = generatorParameters;
        this.maxGeneration = generatorParameters.getGenerationParameters().getMaxGeneration();
        this.minGeneration = generatorParameters.getGenerationParameters().getMinGeneration();
        if (this.maxGeneration > zone.getZoneMatrixSize())
            this.maxGeneration = zone.getZoneMatrixSize();
        if (this.minGeneration > zone.getZoneMatrixSize())
            this.minGeneration = zone.getZoneMatrixSize();

        this.behaviourScheme = behaviourScheme;
        this.random = new Random(randomSeed);
    }

    public List<Agent> generate(double time) {
        List<Agent> agents = new ArrayList<>();

        if (time % (this.generatorParameters.getActiveTime() + this.generatorParameters.getInactiveTime()) < this.generatorParameters.getActiveTime()
                && time - this.lastGenerationTime > this.generatorParameters.getGenerationParameters().getTimeBetweenGenerations()) {
            this.lastGenerationTime = time;
            // generate is on Active time, and it's time to create another generation
            int agentsToCreate = (int) (this.random.nextDouble() * (this.maxGeneration - this.minGeneration)) + this.minGeneration;
            List<Integer> positionsUsed = new ArrayList<>();
            for (int i = 0; i < agentsToCreate; i++) {
                // get position to generate agent, which has to be different from the ones the other agents get to avoid overlap
                int positionIndex;
                do {
                    positionIndex = this.random.nextInt(this.zone.getZoneMatrixSize());
                } while (positionsUsed.contains(positionIndex));
                positionsUsed.add(positionIndex);

                AgentsGeneratorParameters.AgentsParameters agentsParameters = generatorParameters.getAgentsParameters();
                agents.add(
                        new Agent(
                                this.zone.getPositionByIndex(positionIndex),
                                agentsParameters.getMinRadius(),
                                agentsParameters.getMaxRadius(),
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
