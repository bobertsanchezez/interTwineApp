package edu.brown.cs.student.main.server.handlers.passages;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import edu.brown.cs.student.main.server.types.Passage;
import spark.Request;
import spark.Response;
import spark.Route;

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
        MongoDatabase database = mongoClient.getDatabase("interTwine");
        MongoCollection<Document> collection = database.getCollection("passages");
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
            BsonDocument bsonDocument = passage.toBsonDocument();
            Document filter = new Document("id", id);
            Document document = Document.parse(bsonDocument.toJson());
            collection.replaceOne(filter, document);
            return serialize(handlerSuccessResponse(document));
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
