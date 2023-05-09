package edu.brown.cs.student.main.server.handlers.passages;

import java.io.IOException;

import java.io.StringWriter;
import java.io.PrintWriter;

import static com.mongodb.client.model.Filters.eq;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import edu.brown.cs.student.main.server.types.Passage;
import spark.Request;
import spark.Response;

/**
 * Handler class for the redlining API endpoint.
 */
public class PassagePostHandler extends MongoDBHandler {

    public PassagePostHandler(MongoClient mongoClient) {
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
        String data;
        if (request.body().length() != 0) {
            data = request.body();
        } else {
            data = request.queryParams("data");
        }

        if (data == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> must be supplied as query param OR content body (jsonified Passage data)"));
        }
        Moshi moshi = new Moshi.Builder().build();
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
            MongoDatabase database = mongoClient.getDatabase("InterTwine");
            MongoCollection<Document> collection = database.getCollection("passages");
            BsonDocument bsonDocument = passage.toBsonDocument();
            Document newDoc = Document.parse(bsonDocument.toJson());
            Document maybeExistsDoc = collection.find(eq("id", newDoc.get("id")))
                    .first();

            System.out.println("PASSAGE POST PRINTS:");
            System.out.println();
            System.out.println("psgpost raw data:" + data);
            System.out.println();
            System.out.println("psgpost data as doc:" + newDoc.toString());
            System.out.println();

            if (maybeExistsDoc != null) {
                // doc already exists in database
                System.out.println("found passage that already exists:" + maybeExistsDoc.toString());
                System.out.println();
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
                    "Given passage could not be inserted into collection: " + sStackTrace));
        }

    }

}
