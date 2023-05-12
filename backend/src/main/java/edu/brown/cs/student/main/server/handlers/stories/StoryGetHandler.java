package edu.brown.cs.student.main.server.handlers.stories;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;

/**
 * Handler class for GET requests to the stories collection.
 */
public class StoryGetHandler extends MongoDBHandler {

    public StoryGetHandler(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    /**
     * Handles GET requests to the stories collection, involving ...
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
                            "story id <id> is a required query parameter (usage: GET request to .../stories?id=12345)"));
        }
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("stories");
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
            return serialize(handlerSuccessResponse(doc.toJson()));
        }
    }
}
