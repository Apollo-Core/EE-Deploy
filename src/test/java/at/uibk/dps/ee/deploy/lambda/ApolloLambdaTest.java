package at.uibk.dps.ee.deploy.lambda;

import at.uibk.dps.ee.deploy.resources.ReadTestStrings;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test class for the Apollo lambda function.
 */
public class ApolloLambdaTest {

    /**
     * Test the main entry of the lambda function.
     */
    @Test
    public void testLambdaFunction() {
        ApolloLambda apolloLambda = spy(ApolloLambda.class);

        JsonObject json = new JsonObject();
        json.addProperty("configuration", ReadTestStrings.configString);
        json.addProperty("specification", ReadTestStrings.specString);

        JsonObject obj = JsonParser.parseString(ReadTestStrings.inputString).getAsJsonObject();
        obj.keySet().forEach((key) -> json.addProperty(key, obj.get(key).getAsNumber()));

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withBody(json.toString());

        Mockito.doReturn(new JsonObject()).when(apolloLambda).run(obj.toString(), ReadTestStrings.specString, ReadTestStrings.configString);
        APIGatewayProxyResponseEvent response = apolloLambda.handleRequest(input, null);

        assertEquals(Integer.valueOf(200), response.getStatusCode());
        assertEquals(response.getBody(), new JsonObject().toString());
    }

    /**
     * Test missing configuration.
     */
    @Test
    public void testMissingConfiguration() {
        ApolloLambda apolloLambda = spy(ApolloLambda.class);
        JsonObject json = new JsonObject();
        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent().withBody(json.toString());

        Mockito.doReturn(new JsonObject()).when(apolloLambda).run(json.toString(), ReadTestStrings.specString, ReadTestStrings.configString);
        APIGatewayProxyResponseEvent response = apolloLambda.handleRequest(input, null);

        assertEquals(Integer.valueOf(422), response.getStatusCode());
        assertEquals("Missing configuration or specification.", response.getBody());
    }
}
