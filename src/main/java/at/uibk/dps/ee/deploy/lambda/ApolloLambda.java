package at.uibk.dps.ee.deploy.lambda;

import at.uibk.dps.ee.deploy.run.ImplementationRunBare;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.vertx.core.Vertx;

/**
 * {@link ApolloLambda} is used to run Apollo as an AWS Lambda function,
 * getting an Apollo config, spec and input.
 *
 * @author Stefan Pedratscher
 */
public class ApolloLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    /**
     * Runner to run he workflow.
     */
    private ImplementationRunBare runner;

    /**
     * Default constructor.
     */
    public ApolloLambda(){
        runner = new ImplementationRunBare(Vertx.vertx());
    }

    /**
     * The main entry point of the Lambda function representing
     * the Apollo engine.
     *
     * @param input to the lambda function.
     * @param context to access data within the lambda execution environment.
     *
     * @return result of the workflow execution.
     */
    @Override public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input,
        Context context) {

        // Parse API input to json object
        JsonObject json = new Gson().fromJson(input.getBody(), JsonObject.class);

        // Prepare response
        APIGatewayProxyResponseEvent response;

        // Check if required data is present
        if(!json.has("configuration") || !json.has("specification")) {
            response = new APIGatewayProxyResponseEvent()
                .withStatusCode(422)
                .withBody("Missing configuration or specification.");
        } else {
            String configString = json.get("configuration").getAsString();
            String specString = json.get("specification").getAsString();

            // Prepare input
            json.remove("configuration");
            json.remove("specification");
            String inputString = json.toString();

            response = new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(run(inputString, specString, configString).toString());
        }

        return response;
    }

    /**
     * Execute the actual workflow.
     *
     * @param inputString workflow input.
     * @param specString workflow specification.
     * @param configString workflow configuration.
     *
     * @return workflow execution result.
     */
    public JsonObject run(String inputString, String specString, String configString){
        return runner.implement(inputString, specString, configString);
    }
}
