package edu.brown.cs.student.main.server.handlers.passages;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.io.PrintWriter;

import org.bson.Document;
import org.bson.types.ObjectId;

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
 * Handler class for the redlining API endpoint.
 */
public class PassageDeleteHandler extends MongoDBHandler {

    public PassageDeleteHandler(MongoClient mongoClient) {
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
        String id = request.params(":id");
        if (id == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "required parameter <id> was not supplied (usage: DELETE request to localhost:3232/passages/<id>)"));
        } else if (id.length() == 0) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "required parameter <id> was not supplied (usage: DELETE request to localhost:3232/passages/<id>)"));
        }
        try {
            MongoDatabase database = mongoClient.getDatabase("InterTwine");
            // delete the passage
            MongoCollection<Document> psgCollection = database.getCollection("passages");
            Document psgDoc = new Document("id", id);
            DeleteResult res = psgCollection.deleteOne(psgDoc);
            // MongoCollection<Document> storiesCollection =
            // database.getCollection("stories");
            // Document story = storiesCollection
            // .find(Filters.elemMatch("passages", Filters.eq("_id", new
            // ObjectId("passageId")))).first();
            // List<Document> updatedPassages = story.getList("passages",
            // Document.class).stream()
            // .filter(p -> !p.getObjectId("_id").equals(psgDoc.getObjectId(
            // "_id")))
            // .collect(Collectors.toList());
            // Document updatedStory = new Document("passages", updatedPassages);
            // storiesCollection.updateOne(Filters.eq("_id", story.getObjectId("_id")),
            // Updates.set("passages", updatedStory.getList("passages", Document.class)));
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
