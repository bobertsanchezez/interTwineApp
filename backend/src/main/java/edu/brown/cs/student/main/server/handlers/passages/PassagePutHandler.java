package edu.brown.cs.student.main.server.handlers.passages;

import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;

/**
 * Handler class for PUT requests to the passages collection.
 */
public class PassagePutHandler extends MongoDBHandler {

    public PassagePutHandler(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    /**
     * Handles PUT requests to the passages collection, involving ...
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        synchronized (lock) {

            String id = request.params("id");
            String data = request.body();
            if (id == null) {
                return serialize(
                        handlerFailureResponse("error_bad_request",
                                "passage id <id> is a required query parameter (usage: PUT request to .../passages/<id>)"));
            }
            if (data == null) {
                return serialize(handlerFailureResponse("error_bad_request",
                        "data payload <data> must be supplied as query param OR content body (jsonified Passage data)"));
            }
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> psgCollection = database.getCollection("passages");
            MongoCollection<Document> storyCollection = database.getCollection("stories");
            // update (or create) passage
            Document psgDoc = Document.parse(data);
            Document psgFilter = new Document("id", id);
            ReplaceOptions upsertOption = new ReplaceOptions().upsert(true);
            psgCollection.replaceOne(psgFilter, psgDoc, upsertOption);
            // we want to update the story containing the passage
            // 1. find story containing passage
            Document story = storyCollection
                    .find(Filters.elemMatch("passages", Filters.eq("id",
                            psgDoc.get("id"))))
                    .first();
            Document updatedStoryPassages;
            if (story == null) {
                // 2a. no story contains passage yet; find story and add passage
                // this is checked to handle a frontend error
                Document storyFilter = new Document("id", psgDoc.get("story"));
                story = storyCollection.find(storyFilter).first();
                story.getList("passages", Document.class).add(psgDoc);
                updatedStoryPassages = new Document("passages", story.getList("passages", Document.class));
            } else {
                // 2b. story contains passage; update passage in story
                updatedStoryPassages = new Document("passages",
                        story.getList("passages", Document.class).stream()
                                .map(p -> p.get("id").equals(psgDoc.get("id")) ? psgDoc : p)
                                .collect(Collectors.toList()));
            }
            // 3. replace current passages list with updated passages list
            storyCollection.updateOne(Filters.eq("id", story.get("id")),
                    Updates.set("passages", updatedStoryPassages.getList("passages",
                            Document.class)));
            return serialize(handlerSuccessResponse(psgDoc));
        }

    }

}