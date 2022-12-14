package AgentsGenerator;

import Agent.Agent;
import Agent.AgentConstants;
import Environment.Target;
import Utils.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class AgentsGenerator {
    private final AgentsGeneratorZone zone;
    private final double activeTime;
    private final double notActiveTime;
    private final double timeBetweenGenerations;
    private double lastGenerationTime;
    private int minGeneration, maxGeneration;

    // TODO: BEHAVIOUR MODULE
    private List<Target> targets;

    public AgentsGenerator(AgentsGeneratorZone zone, double activeTime, double notActiveTime, double timeBetweenGenerations, int minGeneration, int maxGeneration, List<Target> targets) {
        if (minGeneration > maxGeneration || minGeneration < 0)
            throw new IllegalArgumentException("Bad arguments on generator agent creation limits");

        this.zone = zone;
        this.notActiveTime = notActiveTime;
        this.activeTime = activeTime;
        this.timeBetweenGenerations = timeBetweenGenerations;
        this.minGeneration = minGeneration;
        this.maxGeneration = maxGeneration;
        this.targets = targets;

        if (maxGeneration > zone.getZoneMatrixSize())
            this.maxGeneration = zone.getZoneMatrixSize();
        if (minGeneration > zone.getZoneMatrixSize())
            this.minGeneration = zone.getZoneMatrixSize();
    }

    public List<Agent> generate(double time) {
        List<Agent> agents = new ArrayList<>();
        if (time % (this.activeTime + this.notActiveTime) < this.activeTime && time - this.lastGenerationTime > this.timeBetweenGenerations) {
            this.lastGenerationTime = time;
            // generate is on Active time, and it's time to create another generation
            int agentsToCreate = (int) (Math.random() * (this.maxGeneration - this.minGeneration)) + this.minGeneration;
            List<Integer> positionsUsed = new ArrayList<>();
            for (int i = 0; i < agentsToCreate; i++) {
                // get position to generate agent, which has to be different than the ones the other agents get to avoid overlap
                int positionIndex;
                do {
                    positionIndex = (int) (Math.random() * this.zone.getZoneMatrixSize());
                } while (positionsUsed.contains(positionIndex));
                positionsUsed.add(positionIndex);

                int auxRandom = (int) (Math.random() * 10); // TODO: BORRAR cuando se haga el behaviour module
                agents.add(
                        new Agent(
                                this.zone.getPositionByIndex(positionIndex),
                                AgentConstants.MAX_RADIUS,
                                new ArrayList<>(this.targets.subList(i + auxRandom, 10 + i + auxRandom))
                        )
                );
            }
        }

        return agents;
    }
}
