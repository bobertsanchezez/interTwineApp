package edu.brown.cs.student.main.server.handlers.stories;

import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.io.PrintWriter;

/**
 * Handler class for DELETE requests to the stories collection.
 */
public class StoryDeleteHandler extends MongoDBHandler {

    public StoryDeleteHandler(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
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
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> storyCollection = database.getCollection("stories");
            MongoCollection<Document> psgCollection = database.getCollection("passages");

            // delete story's contained passages
            Document story = storyCollection.find(eq("id", id))
                    .first();
            List<String> storyPassageIds = story.getList("passages", Document.class).stream()
                    .map(p -> p.getString("id"))
                    .collect(Collectors.toList());

            Bson massDelFilter = Filters.in("id", storyPassageIds);
            DeleteResult psgDelRes = psgCollection.deleteMany(massDelFilter);
            System.out.println("IDs to delete: " + storyPassageIds.toString());
            System.out.println("Deleted count: " + psgDelRes.getDeletedCount());

            // delete story
            Document delFilter = new Document("id", id);
            DeleteResult storyDelRes = storyCollection.deleteOne(delFilter);
            // check results
            long count = storyDelRes.getDeletedCount();
            if (count == 0) {
                return serialize(handlerFailureResponse("error_datasource",
                        "Delete failed: no document with id " + id + " contained in the database"));
            } else if (count > 1) {
                return serialize(handlerFailureResponse("error_datasource",
                        "WARNING: Multiple (" + count + ") stories with id " + id + "were deleted!"));
            }
            // return the id of the story that was deleted
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
