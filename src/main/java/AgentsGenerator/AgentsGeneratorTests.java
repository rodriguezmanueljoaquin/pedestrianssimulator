package AgentsGenerator;

import Agent.Agent;
import AgentsBehaviour.BehaviourScheme;
import Environment.Objectives.Objective;
import Environment.Objectives.Target.BorderTarget;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import Utils.Circle;
import Utils.Rectangle;
import Utils.Vector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AgentsGeneratorTests {

    @Test
    public void agentGeneratorInZone() {
        AgentsGeneratorZone agentZone = new AgentsGeneratorZone(new Vector(-5, -5), new Vector(0, 0));
        AgentsGeneratorParameters agentsGeneratorParameters = new AgentsGeneratorParameters(500, 1, "", 0.5, 0.6, 3.0, 5, 4000, 5000);
        BehaviourScheme behaviourScheme = mock(BehaviourScheme.class);
        Objective obj = new BorderTarget("aasdf", new Circle(new Vector(2,2), 2.0), 1.);
        List<Objective> objectives = new ArrayList<>();
        objectives.add(obj);
        when(behaviourScheme.getObjectivesSample()).thenReturn(objectives);


        AgentsGenerator agentsGenerator = new AgentsGenerator("hola", agentZone, agentsGeneratorParameters, behaviourScheme, 1);

        for (double i = 0; i < 500; i += 0.1) {
            List<Agent> agentList = agentsGenerator.generate(i);
            for (Agent agent : agentList) {
                assert agentZone.isPointInside(agent.getPosition());
                for (Agent otherAgent : agentList) {
                    assert Objects.equals(agent.getId(), otherAgent.getId()) || agent.getPosition().distance(otherAgent.getPosition()) > agent.getRadius();
                }
            }
        }

    }
}
