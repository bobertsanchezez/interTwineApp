package edu.brown.cs.student.main.server.handlers.stories;

import java.io.IOException;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import edu.brown.cs.student.main.server.types.Story;
import spark.Request;
import spark.Response;

/**
 * Handler class for the redlining API endpoint.
 */
public class StoryPutHandler extends MongoDBHandler {

    MongoClient mongoClient;

    public StoryPutHandler(MongoClient mongoClient) {
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
        try {

            String id = request.params("id");
            if (id == null) {
                return serialize(
                        handlerFailureResponse("error_bad_request",
                                "story id <id> is a required query parameter (usage: PUT request to .../stories/<id>)"));
            }
            String data = request.body();

            if (data == null) {
                return serialize(handlerFailureResponse("error_bad_request",
                        "data payload <data> must be supplied as query param OR content body (jsonified Story data)"));
            }
            MongoDatabase database = mongoClient.getDatabase("InterTwine");
            MongoCollection<Document> collection = database.getCollection("stories");
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Story> adapter = moshi.adapter(Story.class);
            Story story;
            try {
                story = adapter.fromJson(data);
            } catch (JsonDataException | IOException e) {
                return serialize(handlerFailureResponse("error_bad_request",
                        "data payload <data> could not be converted to Story format"));
            }
            if (story == null) {
                return serialize(handlerFailureResponse("error_bad_request",
                        "data payload <data> was null after json adaptation"));
            }
            try {
                BsonDocument bsonDocument = story.toBsonDocument();
                Document filter = new Document("id", id);
                Document document = Document.parse(bsonDocument.toJson());
                ReplaceOptions upsertOption = new ReplaceOptions().upsert(true);
                collection.replaceOne(filter, document, upsertOption);
                return serialize(handlerSuccessResponse(document));
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                return serialize(handlerFailureResponse("error_datasource",
                        "Given story could not updated: " + sStackTrace));
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            return serialize(handlerFailureResponse("error_datasource",
                    "Given story could not be inserted into collection: " + sStackTrace));
        }
    }

}
