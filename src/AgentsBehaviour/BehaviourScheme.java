package AgentsBehaviour;

import AgentsBehaviour.StateMachine.StateMachine;
import Environment.Exit;
import Environment.Objective;
import Environment.Server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BehaviourScheme {
    // BehaviourScheme defines how agents will act, it is an ordered list detailing the structure of the sequence of actions they will make.
    // Also, it has the state machine that the agents it creates will use, which defines how the transition between states is done.
    /* Example:
        1. Go to the entrance
        2. Search for N products in the market, where 3<N<5
        3. Go to the cashier to pay the products
     */
    private final StateMachine stateMachine;
    private final List<ObjectiveGroup> scheme;
    private final List<Exit> exits;
    private final List<Server> servers;
    private final Random random;

    public BehaviourScheme(StateMachine stateMachine, List<Exit> exits, List<Server> servers) {
        this.stateMachine = stateMachine;
        this.scheme = new ArrayList<>();
        this.exits = exits;
        this.servers = servers;
        this.random = new Random(1);
    }

    public void addObjectiveGroupToScheme(List<Objective> possibleObjectives, Integer minOccurrences, Integer maxOccurrences) {
        this.scheme.add(
                new ObjectiveGroup(possibleObjectives, minOccurrences, maxOccurrences, this.random.nextLong())
        );
    }

    public List<Objective> getObjectivesSample() {
        List<Objective> objectives = new ArrayList<>();
//        for (ObjectiveGroup group : this.scheme) {
//            int objectivesQuantity = (int) (this.random.nextDouble() * (group.getMaxOccurrences() - group.getMinOccurrences())) + group.getMinOccurrences();
//
//            for (int i = 0; i < objectivesQuantity; i++) {
//                objectives.add(group.getRandomObjective());
//            }
//        }

        // TODO: add nearest exit from last objective instead of exits.get(0) y lo mismo con servers
        objectives.add(servers.get(0));
        objectives.add(exits.get(0));


        return objectives;
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    private class ObjectiveGroup {
        private final int minOccurrences, maxOccurrences;
        private final List<Objective> possibleObjectives;
        private final Random random;

        public ObjectiveGroup(List<Objective> possibleObjectives, int minOccurrences, int maxOccurrences, long randomSeed) {
            this.possibleObjectives = possibleObjectives;
            this.minOccurrences = minOccurrences;
            this.maxOccurrences = maxOccurrences;
            this.random = new Random(randomSeed);
        }

        public Objective getRandomObjective() {
            int objectiveIndex = (int) (this.random.nextDouble() * this.possibleObjectives.size());

            return this.possibleObjectives.get(objectiveIndex);
        }

        public int getMinOccurrences() {
            return minOccurrences;
        }

        public int getMaxOccurrences() {
            return maxOccurrences;
        }
    }
}
