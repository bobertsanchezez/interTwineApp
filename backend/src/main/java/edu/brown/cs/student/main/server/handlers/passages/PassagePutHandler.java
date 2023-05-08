package edu.brown.cs.student.main.server.handlers.passages;

import java.io.IOException;

import java.io.StringWriter;
import java.util.Date;
import java.util.stream.Collectors;
import java.io.PrintWriter;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import edu.brown.cs.student.main.server.types.Passage;
import spark.Request;
import spark.Response;

/**
 * Handler class for the redlining API endpoint.
 */
public class PassagePutHandler extends MongoDBHandler {

    public PassagePutHandler(MongoClient mongoClient) {
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
        String id = request.params("id");
        if (id == null) {
            return serialize(
                    handlerFailureResponse("error_bad_request",
                            "passage id <id> is a required query parameter (usage: PUT request to .../passages/<id>)"));
        }
        String data = request.body();

        if (data == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> must be supplied as query param OR content body (jsonified Passage data)"));
        }
        MongoDatabase database = mongoClient.getDatabase("InterTwine");
        MongoCollection<Document> psgCollection = database.getCollection("passages");
        MongoCollection<Document> storyCollection = database.getCollection("stories");
        try {
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
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return serialize(handlerFailureResponse("error_datasource",
                    "Given passage could not updated: " + sStackTrace));
        }

    }

}
