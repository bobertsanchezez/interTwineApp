package edu.brown.cs.student.main.server.handlers.stories;

import java.io.IOException;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.bson.BsonDocument;
import org.bson.Document;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import edu.brown.cs.student.main.server.types.Story;
import spark.Request;
import spark.Response;

/**
 * Handler class for POST requests to the stories collection.
 */
public class StoryPostHandler extends MongoDBHandler {

    public StoryPostHandler(MongoClient mongoClient) {
        super(mongoClient);
    }

    /**
     * Handles POST requests to the stories collection, involving ...
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String data;
        if (request.body().length() != 0) {
            data = request.body();
        } else {
            data = request.queryParams("data");
        }

        if (data == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> must be supplied as query param OR content body (jsonified Story data)"));
        }
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter().nullSafe())
                .build();
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
            MongoDatabase database = mongoClient.getDatabase("InterTwine");
            MongoCollection<Document> collection = database.getCollection("stories");

            BsonDocument bsonDocument = story.toBsonDocument();
            Document newDoc = Document.parse(bsonDocument.toJson());
            Document maybeExistsDoc = collection.find(eq("id", newDoc.get("id")))
                    .first();
            if (maybeExistsDoc != null) {
                // doc already exists in database
                return serialize(handlerSuccessResponse(maybeExistsDoc));
            } else {
                // doc doesn't exist; post it
                collection.insertOne(newDoc);
                return serialize(handlerSuccessResponse(newDoc));
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
