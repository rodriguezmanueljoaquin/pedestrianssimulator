package InputHandling.SimulationParameters;

import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.ServerGroupParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.TargetGroupParameters;
import Utils.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimulationParameters {
    private Map<String, AgentsGeneratorParameters> generatorsParameters;
    private Map<String, TargetGroupParameters> targetGroupsParameters;
    private Map<String, ServerGroupParameters> serverGroupsParameters;

    public SimulationParameters(String JSONPath) {
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(new FileReader(JSONPath));
        } catch (ParseException | IOException e) {
            throw new RuntimeException("Exception when parsing SimulationParametersJSON: " + e);
        }

        initGeneratorsParameters((JSONArray) jsonObject.get("agents_generators"));
        initTargetsParameters((JSONArray) jsonObject.get("targets"));
        initServersParameters((JSONArray) jsonObject.get("servers"));
    }

    private Vector getVectorFromString(String s) {
        String[] tokens = s.split(",");
        return new Vector(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
    }

    private void initServersParameters(JSONArray serversParametersJSON) {
        this.serverGroupsParameters = new HashMap<>();
        for (Object serverParametersObj : serversParametersJSON) {
            JSONObject serverParameters = (JSONObject) serverParametersObj;

            ServerGroupParameters newServerGroupParameters = new ServerGroupParameters(
                    (Double) serverParameters.get("attending_time"),
                    ((Long) serverParameters.get("max_capacity")).intValue(),
                    (Double) serverParameters.get("start_time")
            );
            JSONArray queuesJSON = (JSONArray) serverParameters.get("queues");
            if (queuesJSON != null) {
                for (Object queueParametersObj : queuesJSON) {
                    JSONObject queueParameters = (JSONObject) queueParametersObj;
                    newServerGroupParameters.addQueue(
                            (String) queueParameters.get("id"),
                            getVectorFromString((String) queueParameters.get("start_position")),
                            getVectorFromString((String) queueParameters.get("end_position"))
                    );
                }
            }

            this.serverGroupsParameters.put(
                    (String) serverParameters.get("group_name"),
                    newServerGroupParameters
            );
        }
    }

    private void initTargetsParameters(JSONArray targetsParametersJSON) {
        this.targetGroupsParameters = new HashMap<>();
        for (Object targetParametersObj : targetsParametersJSON) {
            JSONObject targetParameters = (JSONObject) targetParametersObj;

            this.targetGroupsParameters.put(
                    (String) targetParameters.get("group_name"),
                    new TargetGroupParameters(
                            (Double) targetParameters.get("attending_time")
                    )
            );
        }
    }

    private void initGeneratorsParameters(JSONArray generatorsParametersJSON) {
        this.generatorsParameters = new HashMap<>();
        for (Object generatorParametersObj : generatorsParametersJSON) {
            JSONObject generatorParameters = (JSONObject) generatorParametersObj;
            JSONObject generationParameters = (JSONObject) generatorParameters.get("generation");
            JSONObject agentsParameters = (JSONObject) generatorParameters.get("agents");
            this.generatorsParameters.put(
                    (String) generatorParameters.get("group_name"),
                    new AgentsGeneratorParameters(
                            (double) generatorParameters.get("active_time"),
                            (double) generatorParameters.get("inactive_time"),

                            // AgentsParameters
                            (double) agentsParameters.get("min_radius"),
                            (double) agentsParameters.get("max_radius"),
                            (double) agentsParameters.get("max_velocity"),

                            // GenerationParameters
                            (double) generationParameters.get("frequency"),
                            ((Long) generationParameters.get("min_agents")).intValue(),
                            ((Long) generationParameters.get("max_agents")).intValue()
                    )
            );
        }
    }

    public Map<String, AgentsGeneratorParameters> getGeneratorsParameters() {
        return generatorsParameters;
    }

    public Map<String, TargetGroupParameters> getTargetGroupsParameters() {
        return targetGroupsParameters;
    }

    public Map<String, ServerGroupParameters> getServerGroupsParameters() {
        return serverGroupsParameters;
    }
}
