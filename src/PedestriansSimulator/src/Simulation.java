import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Agent> agents;
    private double time, maxTime, dt, dt2;
    private Environment environment;

    public Simulation(List<Agent> agents, double maxTime, double dt, double dt2, Environment environment) {
        this.agents = agents;
        this.maxTime = maxTime;
        this.dt = dt;
        this.dt2 = dt2;
        this.environment = environment;
        this.time = 0;
    }

    public void run(){
        while (this.time < maxTime) {
            // actualizar posiciones
            for (Agent agent : this.agents)
                agent.updatePosition(this.dt);

            CPM.updateAgents(agents, environment);

            // escribir output
            this.time += this.dt;
        }
    }
}
