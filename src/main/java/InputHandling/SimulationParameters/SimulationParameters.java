package InputHandling.SimulationParameters;

import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.ServerGroupParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.TargetGroupParameters;
import Utils.Random.RandomGenerator;
import Utils.Random.UniformRandom;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static InputHandling.ParametersNames.*;

public class SimulationParameters {
    private Map<String, AgentsGeneratorParameters> generatorsParameters;
    private Map<String, TargetGroupParameters> targetGroupsParameters;
    private Map<String, ServerGroupParameters> serverGroupsParameters;
    private final Long maxTime;
    private Long evacuationTime = null;

    public SimulationParameters(String JSONPath, Random seedGenerator) {
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(new FileReader(JSONPath));
        } catch (ParseException | IOException e) {
            throw new RuntimeException("Exception when parsing SimulationParametersJSON: " + e);
        }

        this.maxTime = (Long) jsonObject.get(EVACUATE_TIME_KEY);
        if(jsonObject.containsKey(EVACUATE_TIME_KEY))
            this.evacuationTime = (Long) jsonObject.get(EVACUATE_TIME_KEY);
        this.initGeneratorsParameters(seedGenerator, (JSONArray) jsonObject.get(GENERATORS_KEY));
        this.initTargetsParameters(seedGenerator, (JSONArray) jsonObject.get(TARGETS_KEY));
        this.initServersParameters(seedGenerator, (JSONArray) jsonObject.get(SERVERS_KEY));
    }

    private void initGeneratorsParameters(Random seedGenerator, JSONArray generatorsParametersJSON) {
        this.generatorsParameters = new HashMap<>();
        for (Object generatorParametersObj : generatorsParametersJSON) {
            JSONObject generatorParameters = (JSONObject) generatorParametersObj;
            JSONObject generationParameters = (JSONObject) generatorParameters.get(GENERATION_KEY);
            JSONObject agentsParameters = (JSONObject) generatorParameters.get(AGENTS_KEY);
            this.generatorsParameters.put(
                    (String) generatorParameters.get(GROUP_NAME_KEY),
                    new AgentsGeneratorParameters(
                            (double) generatorParameters.get(ACTIVE_TIME_KEY),
                            (double) generatorParameters.get(INACTIVE_TIME_KEY),
                            (String) generatorParameters.get(BEHAVIOUR_SCHEME_KEY),

                            // AgentsParameters
                            (double) agentsParameters.get(MIN_RADIUS_KEY),
                            (double) agentsParameters.get(MAX_RADIUS_KEY),
                            (double) agentsParameters.get(MAX_VELOCITY_KEY),

                            // GenerationParameters
                            (double) generationParameters.get(FREQUENCY_KEY),
                            new UniformRandom(
                                    seedGenerator.nextLong(),
                                    ((Long) generationParameters.get(MIN_AGENTS_KEY)).intValue(),
                                    ((Long) generationParameters.get(MAX_AGENTS_KEY)).intValue()
                            )
                    )
            );
        }
    }

    private void initTargetsParameters(Random seedGenerator, JSONArray targetsParametersJSON) {
        this.targetGroupsParameters = new HashMap<>();
        for (Object targetParametersObj : targetsParametersJSON) {
            JSONObject targetParameters = (JSONObject) targetParametersObj;

            this.targetGroupsParameters.put(
                    (String) targetParameters.get(GROUP_NAME_KEY),
                    new TargetGroupParameters(
                            new UniformRandom(
                                    seedGenerator.nextLong(),
                                    (Double) targetParameters.get(ATTENDING_TIME_KEY),
                                    (Double) targetParameters.get(ATTENDING_TIME_KEY) + 10
                            )
                    )
            );
        }
    }

    private void initServersParameters(Random seedGenerator, JSONArray serversParametersJSON) {
        this.serverGroupsParameters = new HashMap<>();
        for (Object serverParametersObj : serversParametersJSON) {
            JSONObject serverParameters = (JSONObject) serverParametersObj;
            //TODO: Insert other value from random
            //TODO: UniformRandom could also recieve attending time and variance
            //TODO: Ask Mr. Parisi how to handle Exponential Random as lambda gives weird cases.
            ServerGroupParameters newServerGroupParameters = new ServerGroupParameters(
                    new UniformRandom(
                            seedGenerator.nextLong(),
                            (Double) serverParameters.get(ATTENDING_TIME_KEY),
                            (Double) serverParameters.get(ATTENDING_TIME_KEY) + 10
                    ),
                    ((Long) serverParameters.get(MAX_CAPACITY_KEY)).intValue(),
                    (Double) serverParameters.get(START_TIME_KEY)
            );

            this.serverGroupsParameters.put(
                    (String) serverParameters.get(GROUP_NAME_KEY),
                    newServerGroupParameters
            );
        }
    }

    public Long getMaxTime() {
        return this.maxTime;
    }

    public long getEvacuationTime() {
        return this.evacuationTime;
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
