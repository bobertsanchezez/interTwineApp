package edu.brown.cs.student.main.server.handlers.passages;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;

/**
 * Handler class for GET requests to the passages collection.
 */
public class PassageGetHandler extends MongoDBHandler {

    public PassageGetHandler(MongoClient mongoClient) {
        super(mongoClient);
    }

    /**
     * 
     * Handles GET requests to the passages collection, involving ...
     * 
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.queryParams("id");
        if (id == null) {
            return serialize(
                    handlerFailureResponse("error_bad_request",
                            "passage id <id> is a required query parameter (usage: GET request to .../passages?id=12345)"));
        }
        MongoDatabase database = mongoClient.getDatabase("InterTwine");
        MongoCollection<Document> collection = database.getCollection("passages");
        Document doc;
        try {
            doc = collection.find(eq("id", id))
                    .first();
        } catch (MongoException e) {
            return serialize(
                    handlerFailureResponse("error_datasource", "id " + id + " does not exist in the database"));
        }
        if (doc == null) {
            return serialize(
                    handlerFailureResponse("error_datasource", "id " + id + " does not exist in the database"));
        } else {
            if (doc.getBoolean("claimed")) {
                String userId = doc.getString("user");
                return serialize(
                        claimFailureResponse("error_claimed", userId,
                                "user " + userId + " has already claimed the passage"));
            }
            return serialize(handlerSuccessResponse(doc.toJson()));
        }

    }

    private Map<String, Object> claimFailureResponse(String responseType, String userId, String errorMessage) {
        Map<String, Object> responses = new HashMap<>();
        responses.put("result", responseType);
        responses.put("user", userId);
        responses.put("errorMessage", errorMessage);
        return responses;
    }
}