package InputHandling.SimulationParameters;

import InputHandling.SimulationParameters.AuxiliarClasses.AgentsGeneratorParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.ServerGroupParameters;
import InputHandling.SimulationParameters.AuxiliarClasses.TargetGroupParameters;
import Utils.Random.ExponentialRandom;
import Utils.Random.GaussianRandom;
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

public class SimulationParametersParser {
    private Map<String, AgentsGeneratorParameters> generatorsParameters;
    private Map<String, TargetGroupParameters> targetGroupsParameters;
    private Map<String, ServerGroupParameters> serverGroupsParameters;
    private final Long maxTime;
    private Long evacuationTime = null;
    private double agentsMaximumMostPossibleRadius = 0;

    public SimulationParametersParser(String JSONPath, Random seedGenerator) {
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

    private RandomGenerator getRandomGenerator(JSONObject parameters, long seed) {
        RandomGenerator randomGenerator;

        switch ((String) parameters.get(DISTRIBUTION_TYPE_KEY)) {
            case DISTRIBUTION_UNIFORM_KEY:
                if(parameters.get(DISTRIBUTION_MIN_KEY) == null || parameters.get(DISTRIBUTION_MAX_KEY) == null)
                    throw new IllegalArgumentException("Distribution min or max values not found on the parameters JSON file when uniform distribution specified.");

                randomGenerator = new UniformRandom(seed, (double) parameters.get(DISTRIBUTION_MIN_KEY), (double) parameters.get(DISTRIBUTION_MAX_KEY));
                break;

            case DISTRIBUTION_GAUSSIAN_KEY:
                if(parameters.get(DISTRIBUTION_MEAN_KEY) == null || parameters.get(DISTRIBUTION_STD_KEY) == null)
                    throw new IllegalArgumentException("Distribution mean or std values not found on the parameters JSON file when gaussian distribution specified.");

                randomGenerator = new GaussianRandom(seed, (double) parameters.get(DISTRIBUTION_MEAN_KEY), (double) parameters.get(DISTRIBUTION_STD_KEY));
                break;

            case DISTRIBUTION_EXPONENTIAL_KEY:
                if(parameters.get(DISTRIBUTION_MEAN_KEY) == null)
                    throw new IllegalArgumentException("Distribution std values not found on the parameters JSON file when exponential distribution specified.");

                randomGenerator = new ExponentialRandom(seed, (double) parameters.get(DISTRIBUTION_MEAN_KEY));
                break;

            default:
                throw new IllegalArgumentException("Distribution type not supported found on the parameters JSON file.");
        }

        return randomGenerator;
    }

    private JSONObject getOrThrowMissingException(JSONObject from, String key) {
        if(from.get(key) == null)
            throw new IllegalArgumentException("Parameter: " + key + " not found.");

        return (JSONObject) from.get(key);
    }

    private void initGeneratorsParameters(Random seedGenerator, JSONArray generatorsParametersJSON) {
        this.generatorsParameters = new HashMap<>();
        for (Object generatorParametersObj : generatorsParametersJSON) {
            JSONObject generatorParameters = (JSONObject) generatorParametersObj;
            JSONObject generationParameters = this.getOrThrowMissingException(generatorParameters, GENERATION_KEY);
            JSONObject agentsParameters = this.getOrThrowMissingException(generatorParameters, AGENTS_KEY);

            this.generatorsParameters.put(
                    (String) generatorParameters.get(GROUP_NAME_KEY),
                    new AgentsGeneratorParameters(
                            (double) generatorParameters.get(ACTIVE_TIME_KEY),
                            (double) generatorParameters.get(INACTIVE_TIME_KEY),
                            (String) generatorParameters.get(BEHAVIOUR_SCHEME_KEY),

                            // AgentsParameters
                            this.getRandomGenerator(this.getOrThrowMissingException(agentsParameters, MIN_RADIUS_DISTRIBUTION_KEY), seedGenerator.nextLong()),
                            this.getRandomGenerator(this.getOrThrowMissingException(agentsParameters, MAX_RADIUS_DISTRIBUTION_KEY), seedGenerator.nextLong()),
                            (double) agentsParameters.get(MAX_VELOCITY_KEY),

                            // GenerationParameters
                            (double) generationParameters.get(FREQUENCY_KEY),
                            this.getRandomGenerator(this.getOrThrowMissingException(generationParameters, GENERATION_QUANTITY_DISTRIBUTION_KEY), seedGenerator.nextLong())
                    )
            );
        }

        this.agentsMaximumMostPossibleRadius = this.generatorsParameters.values().stream()
                .map(AgentsGeneratorParameters::getAgentsParameters)
                .map(AgentsGeneratorParameters.AgentsParameters::getMaxRadiusGenerator)
                .map(RandomGenerator::getHighestMostPossibleValue)
                .mapToDouble(Double::doubleValue).max().getAsDouble();
    }

    private void initTargetsParameters(Random seedGenerator, JSONArray targetsParametersJSON) {
        this.targetGroupsParameters = new HashMap<>();
        for (Object targetParametersObj : targetsParametersJSON) {
            JSONObject targetParameters = (JSONObject) targetParametersObj;

            this.targetGroupsParameters.put(
                    (String) targetParameters.get(GROUP_NAME_KEY),
                    new TargetGroupParameters(
                            this.getRandomGenerator(this.getOrThrowMissingException(targetParameters, ATTENDING_TIME_DISTRIBUTION_KEY), seedGenerator.nextLong())
                    )
            );
        }
    }

    private void initServersParameters(Random seedGenerator, JSONArray serversParametersJSON) {
        this.serverGroupsParameters = new HashMap<>();
        for (Object serverParametersObj : serversParametersJSON) {
            JSONObject serverParameters = (JSONObject) serverParametersObj;

            ServerGroupParameters newServerGroupParameters = new ServerGroupParameters(
                    this.getRandomGenerator(this.getOrThrowMissingException(serverParameters, ATTENDING_TIME_DISTRIBUTION_KEY), seedGenerator.nextLong()),
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
        return this.generatorsParameters;
    }

    public double getAgentsMostPossibleMaxRadius() {
        return this.agentsMaximumMostPossibleRadius;
    }

    public double getAgentsHighestMaxVelocity() {
        return this.generatorsParameters.values().stream()
                .map(AgentsGeneratorParameters::getAgentsParameters)
                .map(AgentsGeneratorParameters.AgentsParameters::getMaxVelocity)
                .mapToDouble(Double::doubleValue).max().getAsDouble();
    }

    public Map<String, TargetGroupParameters> getTargetGroupsParameters() {
        return this.targetGroupsParameters;
    }

    public Map<String, ServerGroupParameters> getServerGroupsParameters() {
        return this.serverGroupsParameters;
    }
}
