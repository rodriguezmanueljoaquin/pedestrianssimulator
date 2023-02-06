package AgentsGenerator;

import Agent.Agent;
import AgentsBehaviour.BehaviourScheme;
import Environment.Objectives.Objective;
import Environment.Objectives.Target;
import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import Utils.Vector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AgentsGeneratorTests {

    @Test public void agentGeneratorInZone(){
        AgentsGeneratorZone agentZone = new AgentsGeneratorZone(new Vector(0,0),new Vector(5,5));
        AgentsGeneratorParameters agentsGeneratorParameters = new AgentsGeneratorParameters(500,1,"",0.5,2.0,3.0,5,1000,5000);
        BehaviourScheme behaviourScheme = mock(BehaviourScheme.class);
        Objective obj = new Target("aasdf",new Vector(2,2),1.);
        List<Objective> objectives = new ArrayList<>();
        objectives.add(obj);
        when(behaviourScheme.getObjectivesSample()).thenReturn(objectives);



        AgentsGenerator agentsGenerator = new AgentsGenerator("hola",agentZone,agentsGeneratorParameters,behaviourScheme,1);


        for(double i=0;i< 500;i +=0.1){
            List<Agent> agentList = agentsGenerator.generate(i);
            for(Agent agent : agentList){
                assert agentZone.isPointInside(agent.getPosition());
//                assert agentZone.isPointInside(agent.getPosition().add(new Vector(agent.getRadius(),agent.getRadius())));
//                assert agentZone.isPointInside(agent.getPosition().add(new Vector(-agent.getRadius(),agent.getRadius())));
//                assert agentZone.isPointInside(agent.getPosition().add(new Vector(-agent.getRadius(),-agent.getRadius())));
//                assert agentZone.isPointInside(agent.getPosition().add(new Vector(-agent.getRadius(),-agent.getRadius())));
            }
        }
    }
}
