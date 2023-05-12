package edu.brown.cs.student.main.server.handlers.passages;

import java.io.StringWriter;
import java.util.stream.Collectors;
import java.io.PrintWriter;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;

/**
 * Handler class for DELETE requests to the passages collection.
 */
public class PassageDeleteHandler extends MongoDBHandler {

    public PassageDeleteHandler(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    /**
     * Handles DELETE requests to the passages collection, involving ...
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
                    "required parameter <id> was not supplied (usage: DELETE request to localhost:3232/passages/<id>)"));
        } else if (id.length() == 0) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "required parameter <id> was not supplied (usage: DELETE request to localhost:3232/passages/<id>)"));
        }
        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> psgCollection = database.getCollection("passages");
            MongoCollection<Document> storyCollection = database.getCollection("stories");
            // delete the passage
            Document psgDoc = new Document("id", id);
            DeleteResult res = psgCollection.deleteOne(psgDoc);
            Document story = storyCollection
                    .find(Filters.elemMatch("passages", Filters.eq("id",
                            psgDoc.get("id"))))
                    .first();
            Document updatedStoryPassages;

            updatedStoryPassages = new Document("passages",
                    story.getList("passages", Document.class).stream()
                            .filter(p -> !p.get("id").equals(psgDoc.get("id")))
                            .collect(Collectors.toList()));

            storyCollection.updateOne(Filters.eq("id", story.get("id")),
                    Updates.set("passages", updatedStoryPassages.getList("passages",
                            Document.class)));
            if (res.getDeletedCount() == 0) {
                return serialize(handlerFailureResponse("error_datasource",
                        "Delete failed: no document with id " + id + " contained in the database"));
            } else if (res.getDeletedCount() > 1) {
                return serialize(handlerFailureResponse("error_datasource",
                        "WARNING: DELETED MULTIPLE PASSAGES!"));
            }

            return serialize(handlerSuccessResponse(id));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return serialize(handlerFailureResponse("error_datasource",
                    "Given passage could not be deleted from collection: " + sStackTrace));
        }

    }

}
