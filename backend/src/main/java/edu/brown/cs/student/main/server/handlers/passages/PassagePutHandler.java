package edu.brown.cs.student.main.server.handlers.passages;

import java.io.IOException;

import java.io.StringWriter;
import java.util.Date;
import java.util.stream.Collectors;
import java.io.PrintWriter;

import org.bson.BsonDocument;
import org.bson.Document;

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
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter().nullSafe())
                .build();
        JsonAdapter<Passage> adapter = moshi.adapter(Passage.class);
        Passage passage;
        try {
            passage = adapter.fromJson(data);
        } catch (JsonDataException | IOException e) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> could not be converted to Passage format"));
        }
        if (passage == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> was null after json adaptation"));
        }
        try {
            // update (or create) passage
            BsonDocument psgBsonDoc = passage.toBsonDocument();
            Document filter = new Document("id", id);
            Document psgDoc = Document.parse(psgBsonDoc.toJson());
            ReplaceOptions upsertOption = new ReplaceOptions().upsert(true);
            psgCollection.replaceOne(filter, psgDoc, upsertOption);
            // update story containing passage
            // 1. find story containing passage
            // Document story = storyCollection
            // .find(Filters.elemMatch("passages", Filters.eq("_id",
            // psgDoc.getObjectId("_id")))).first();

            // System.out.println("found story:" + story.toJson());
            // // 2. make a doc containing updated passages list for story
            // System.out.println("passage stuff:" + psgDoc.toJson());
            // Document updatedStoryPassages = new Document("passages",
            // story.getList("passages", Document.class).stream()
            // .map(p -> p.getObjectId("_id").equals(psgDoc.getObjectId("_id")) ? psgDoc :
            // p)
            // .collect(Collectors.toList()));
            // // 3. replace current passages list with updated passages list
            // storyCollection.updateOne(Filters.eq("_id", story.getObjectId("_id")),
            // Updates.set("passages", updatedStoryPassages.getList("passages",
            // Document.class)));
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
