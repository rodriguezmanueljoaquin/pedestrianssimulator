package AgentsGenerator;

import Agent.Agent;
import Agent.AgentConstants;
import AgentsBehaviour.BehaviourScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgentsGenerator {
    private final AgentsGeneratorZone zone;
    private final double activeTime;
    private final double notActiveTime;
    private final double timeBetweenGenerations;
    private final BehaviourScheme behaviourScheme;
    private int minGeneration, maxGeneration;
    private double lastGenerationTime;
    private final Random random;

    public AgentsGenerator(AgentsGeneratorZone zone, double activeTime, double notActiveTime, double timeBetweenGenerations,
                           int minGeneration, int maxGeneration, BehaviourScheme behaviourScheme, long randomSeed) {
        if (minGeneration > maxGeneration || minGeneration < 0)
            throw new IllegalArgumentException("Bad arguments on generator agent creation limits");

        this.zone = zone;
        this.notActiveTime = notActiveTime;
        this.activeTime = activeTime;
        this.timeBetweenGenerations = timeBetweenGenerations;
        this.minGeneration = minGeneration;
        this.maxGeneration = maxGeneration;
        this.behaviourScheme = behaviourScheme;

        if (maxGeneration > zone.getZoneMatrixSize())
            this.maxGeneration = zone.getZoneMatrixSize();
        if (minGeneration > zone.getZoneMatrixSize())
            this.minGeneration = zone.getZoneMatrixSize();

        this.random = new Random(randomSeed);
    }

    public List<Agent> generate(double time) {
        List<Agent> agents = new ArrayList<>();
        if (time % (this.activeTime + this.notActiveTime) < this.activeTime && time - this.lastGenerationTime > this.timeBetweenGenerations) {
            this.lastGenerationTime = time;
            // generate is on Active time, and it's time to create another generation
            int agentsToCreate = (int) (this.random.nextDouble() * (this.maxGeneration - this.minGeneration)) + this.minGeneration;
            List<Integer> positionsUsed = new ArrayList<>();
            for (int i = 0; i < agentsToCreate; i++) {
                // get position to generate agent, which has to be different from the ones the other agents get to avoid overlap
                int positionIndex;
                do {
                    positionIndex = (int) (this.random.nextDouble() * this.zone.getZoneMatrixSize());
                } while (positionsUsed.contains(positionIndex));
                positionsUsed.add(positionIndex);
                agents.add(
                        new Agent(this.zone.getPositionByIndex(positionIndex),
                                AgentConstants.MAX_RADIUS, this.behaviourScheme.getStateMachine(),
                                this.behaviourScheme.getObjectivesSample()
                        )
                );
            }
        }
        return agents;
    }
}
