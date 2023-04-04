package AgentsBehaviour.PredefinedBehaviours;

import AgentsBehaviour.BehaviourScheme;
import AgentsBehaviour.StateMachine.SuperMarketClientSM;
import Environment.Objectives.Exit;
import Environment.Objectives.Server.Server;
import Environment.Objectives.Target.Target;
import GraphGenerator.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MarketClientBehaviourScheme {
    public static BehaviourScheme get(Graph graph, List<Exit> exits,
                                      Map<String, List<Server>> serversMap, Map<String, List<Target>> targetsMap,
                                      double agentsMaximumMostPossibleRadius, Random random) {
        BehaviourScheme marketClientBehaviourScheme = new BehaviourScheme(new SuperMarketClientSM(graph), exits,
                graph, agentsMaximumMostPossibleRadius, random);

        if (targetsMap.containsKey("PRODUCT1"))
            marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCT1")), 2, 3);

        if (targetsMap.containsKey("PRODUCT2"))
            marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(targetsMap.get("PRODUCT2")), 1, 1);

        if (serversMap.containsKey("CASHIER"))
            marketClientBehaviourScheme.addObjectiveGroupToScheme(new ArrayList<>(serversMap.get("CASHIER")), 1, 1);

        return marketClientBehaviourScheme;
    }
}
