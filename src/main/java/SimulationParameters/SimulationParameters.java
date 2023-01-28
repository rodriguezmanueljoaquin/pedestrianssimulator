package SimulationParameters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// TODO: Tirar excepción si json mal formateado? Por ejemplo algun attending time tiene string en lugar de double
// TODO: Mejorar tratamiento de doubles, ahora si en el JSON llega un 1, o un 1. larga excepcion por que lo reconoce como Long que no puede ser casteado a double
// TODO: Mejorar el acceso a las variables del JSON, usar CONSTANTES
public class SimulationParameters {
    private AgentsParameters agentsParameters;
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

        initAgentsParameters((JSONObject) jsonObject.get("agents"));
        initGeneratorsParameters((JSONArray) jsonObject.get("agents_generators"));
        initTargetsParameters((JSONArray) jsonObject.get("targets"));
        initServersParameters((JSONArray) jsonObject.get("servers"));
    }

    private void initServersParameters(JSONArray serversParametersJSON) {
        this.serverGroupsParameters = new HashMap<>();
        for(Object serverParametersObj : serversParametersJSON) {
            JSONObject serverParameters = (JSONObject) serverParametersObj;

            this.serverGroupsParameters.put(
                    (String) serverParameters.get("group_name"),
                    new ServerGroupParameters(
                            (Double) serverParameters.get("attending_time"),
                            ((Long) serverParameters.get("max_attendants")).intValue()
                    )
            );
        }
    }

    private void initTargetsParameters(JSONArray targetsParametersJSON) {
        this.targetGroupsParameters = new HashMap<>();
        for(Object targetParametersObj : targetsParametersJSON) {
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
        for(Object generatorParametersObj : generatorsParametersJSON) {
            JSONObject generatorParameters = (JSONObject) generatorParametersObj;
            JSONObject generationParameters = (JSONObject) generatorParameters.get("generation");
            this.generatorsParameters.put(
                    (String) generatorParameters.get("name"),
                    new AgentsGeneratorParameters(
                            (double) generatorParameters.get("active_time"),
                            (double) generatorParameters.get("inactive_time"),

                            (double) generationParameters.get("frequency"),
                            ((Long) generationParameters.get("min_agents")).intValue(),
                            ((Long) generationParameters.get("max_agents")).intValue()
                    )
            );
        }
    }

    private void initAgentsParameters(JSONObject agentParameters) {
        this.agentsParameters = new AgentsParameters(
                (double) agentParameters.get("max_radius"),
                (double) agentParameters.get("min_radius"),
                (double) agentParameters.get("max_velocity")
        );
    }

    public AgentsParameters getAgentsParameters() {
        return agentsParameters;
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
