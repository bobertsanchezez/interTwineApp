package edu.brown.cs.student.main.server.handlers.stories;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Handler class for DELETE requests to the stories collection.
 */
public class StoryDeleteHandler extends MongoDBHandler {

    public StoryDeleteHandler(MongoClient mongoClient) {
        super(mongoClient);
    }

    /**
     * Handles DELETE requests to the stories collection, involving
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {

        String id = request.params(":id");
        if (id == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "required parameter <id> was not supplied (usage: DELETE request to localhost:3232/stories/<id>)"));
        } else if (id.length() == 0) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "required parameter <id> was not supplied (usage: DELETE request to localhost:3232/stories/<id>)"));
        }
        try {
            MongoDatabase database = mongoClient.getDatabase("InterTwine");
            MongoCollection<Document> collection = database.getCollection("stories");
            Document toDelete = new Document("id", id);
            DeleteResult res = collection.deleteOne(toDelete);
            long count = res.getDeletedCount();
            if (count == 0) {
                return serialize(handlerFailureResponse("error_datasource",
                        "Delete failed: no document with id " + id + " contained in the database"));
            } else if (count > 1) {
                return serialize(handlerFailureResponse("error_datasource",
                        "WARNING: Multiple (" + count + ") stories with id " + id + "were deleted!"));
            }
            return serialize(handlerSuccessResponse(id));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return serialize(handlerFailureResponse("error_datasource",
                    "Given story could not be deleted from collection: " + sStackTrace));
        }

    }

}
