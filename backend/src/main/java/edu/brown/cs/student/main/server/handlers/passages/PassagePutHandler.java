package edu.brown.cs.student.main.server.handlers.passages;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.client.MongoClient;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the redlining API endpoint.
 */
public class PassagePutHandler extends MongoDBHandler {

    public PassagePutHandler(MongoClient mongoClient) {
        super(mongoClient);
    }

    /**
     * 
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        int size = request.queryParams().size();
        if (size == 0) {
            return serialize(handlerFailureResponse(null, null));
        }

        return serialize(handlerSuccessResponse(null));
    }

}
