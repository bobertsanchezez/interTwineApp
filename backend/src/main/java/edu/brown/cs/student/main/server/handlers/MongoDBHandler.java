package edu.brown.cs.student.main.server.handlers;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.client.MongoClient;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A generalized handler that uses a connection to a MongoDB database.
 */
public class MongoDBHandler implements Route {
    protected MongoClient mongoClient;
    protected String databaseName;

    /**
     * Constructor for a handler that connects to MongoDB.
     * 
     * @param mongoClient the MongoDB connection
     */
    public MongoDBHandler(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    /**
     * A general handler method that should be overridden by extending classes.
     * Returns null handler responses.
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return null handler responses
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

    /**
     * Returns a Map containing a success response to be converted to JSON.
     * 
     * @return a Map<String,Object> containing response fields
     */
    protected Map<String, Object> handlerSuccessResponse(Object data) {
        Map<String, Object> responses = new HashMap<>();
        responses.put("result", "success");
        responses.put("data", data);
        return responses;
    }

    /**
     * Returns a Map containing a failure response to be converted to JSON.
     *
     * @return a Map<String,Object> containing response fields
     */
    protected Map<String, Object> handlerFailureResponse(String responseType, String errorMessage) {
        Map<String, Object> responses = new HashMap<>();
        responses.put("result", responseType);
        responses.put("errorMessage", errorMessage);
        return responses;
    }

    /**
     * Serializes the given Map<String, Object> into JSON to be returned to a
     * requesting user.
     * Utilizes a Moshi adapter capable of adapting Dates.
     * 
     * @return string representation of the Map in JSON format
     */
    protected String serialize(Map<String, Object> response) {
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter().nullSafe())
                .build();
        Type mapOfJSONResponse = Types.newParameterizedType(Map.class,
                String.class,
                Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfJSONResponse);
        return adapter.toJson(response);
    }
}
