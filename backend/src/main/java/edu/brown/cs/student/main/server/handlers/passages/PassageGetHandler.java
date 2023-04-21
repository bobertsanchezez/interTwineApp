package edu.brown.cs.student.main.server.handlers.passages;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class for the redlining API endpoint.
 */
public class PassageGetHandler implements Route {

    public PassageGetHandler() {
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

    /**
     * Returns a Map containing a success response to be converted to JSON.
     *
     * @return a Map<String,Object> containing response fields
     */
    private Map<String, Object> handlerSuccessResponse(Object changeMe) {
        Map<String, Object> responses = new HashMap<>();
        responses.put("result", "success");
        responses.put("data", changeMe);
        return responses;
    }

    /**
     * Returns a Map containing a failure response to be converted to JSON.
     *
     * @return a Map<String,Object> containing response fields
     */
    private Map<String, Object> handlerFailureResponse(String responseType, String errorMessage) {
        Map<String, Object> responses = new HashMap<>();
        responses.put("result", responseType);
        responses.put("errorMessage", errorMessage);
        return responses;
    }

    /**
     * Serializes the given Map<String, Object> into JSON to be returned to a
     * requesting user.
     *
     * @return string representation of the Map in JSON format
     */
    public static String serialize(Map<String, Object> response) {
        Moshi moshi = new Moshi.Builder().build();
        Type mapOfJSONResponse = Types.newParameterizedType(Map.class,
                String.class,
                Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapOfJSONResponse);
        return adapter.toJson(response);
    }

}
