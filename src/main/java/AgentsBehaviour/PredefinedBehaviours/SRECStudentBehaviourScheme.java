package AgentsBehaviour.PredefinedBehaviours;

import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.StateMachine.DefaultSM;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Target.Target;
import GraphGenerator.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SRECStudentBehaviourScheme {
    // TODO: IMPROVE!
    public static BehaviourScheme get(Graph graph, List<Exit> exits, Map<String, List<Server>> serversMap, Map<String, List<Target>> targetsMap, double agentsMaximumMostPossibleRadius, Random random) {
        BehaviourScheme behaviourScheme = new BehaviourScheme(new DefaultSM(graph), exits,
                graph, agentsMaximumMostPossibleRadius, random);

        if (serversMap.containsKey("CHECK IN"))
            behaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(serversMap.get("CHECK IN")), 1, 1);
        if (serversMap.containsKey("COFFEE MACHINE"))
            behaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(serversMap.get("COFFEE MACHINE")), 0, 1);
        if (serversMap.containsKey("VENDING MACHINE"))
            behaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(serversMap.get("VENDING MACHINE")), 0, 1);
        if (serversMap.containsKey("LIBRARY"))
            behaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("LIBRARY")), 0, 3);
        if (targetsMap.containsKey("SUM1"))
            behaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("SUM 1")), 0, 1);

        return behaviourScheme;
    }
}
