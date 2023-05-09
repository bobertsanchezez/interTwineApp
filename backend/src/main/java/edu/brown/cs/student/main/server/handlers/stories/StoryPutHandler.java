package edu.brown.cs.student.main.server.handlers.stories;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;

/**
 * Handler class for PUT requests to the stories collection.
 */
public class StoryPutHandler extends MongoDBHandler {

    public StoryPutHandler(MongoClient mongoClient) {
        super(mongoClient);
    }

    /**
     * Handles PUT requests to the stories collection, involving ...
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String id = request.params("id");
        String data = request.body();
        if (id == null) {
            return serialize(
                    handlerFailureResponse("error_bad_request",
                            "story id <id> is a required query parameter (usage: PUT request to .../stories/<id>)"));
        }
        if (data == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> must be supplied as query param OR content body (jsonified Story data)"));
        }
        MongoDatabase database = mongoClient.getDatabase("InterTwine");
        MongoCollection<Document> collection = database.getCollection("stories");
        try {
            Document filter = new Document("id", id);
            Document storyDoc = Document.parse(data);
            ReplaceOptions upsertOption = new ReplaceOptions().upsert(true);
            collection.replaceOne(filter, storyDoc, upsertOption);
            return serialize(handlerSuccessResponse(storyDoc));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return serialize(handlerFailureResponse("error_datasource",
                    "Given story could not be updated: " + sStackTrace));
        }
    }

}
