package Agent;

import Environment.Target;
import Utils.Rectangle;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class AgentsGenerator {
    private Rectangle zone;
    private double activeTime, notActiveTime, timeBetweenGenerations, lastGenerationTime;
    private int minGeneration, maxGeneration;

    // TODO: BEHAVIOUR MODULE
    private List<Target> targets;

    public AgentsGenerator(Rectangle zone, double activeTime, double notActiveTime, double timeBetweenGenerations, int minGeneration, int maxGeneration, List<Target> targets) {
        if(minGeneration > maxGeneration || minGeneration < 0)
            throw new IllegalArgumentException("Bad arguments on generator agent creation limits");

        this.zone = zone;
        this.notActiveTime = notActiveTime;
        this.activeTime = activeTime;
        this.timeBetweenGenerations = timeBetweenGenerations;
        this.minGeneration = minGeneration;
        this.maxGeneration = maxGeneration;
        this.targets = targets;
    }

    public List<Agent> generate(double time) {
        List<Agent> agents = new ArrayList<>();
        if(time % (this.activeTime + this.notActiveTime) < this.activeTime && time - this.lastGenerationTime > this.timeBetweenGenerations) {
            this.lastGenerationTime = time;
            // generate is on Active time, and it's time to create another generation
            int agentsToCreate = (int) (Math.random() * (this.maxGeneration - this.minGeneration)) + this.minGeneration;
            for (int i = 0; i < agentsToCreate; i++) {
                // TODO check new agent doesn´t collide with existing one
                agents.add(new Agent(this.zone.getRandomPointInside(), 0.5, new ArrayList<>(this.targets.subList(i, 10+i))));
            }
        }

        return agents;
    }
}
