package edu.brown.cs.student.main.server.handlers.passages;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
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
        String newText = request.body();
        MongoDatabase database = mongoClient.getDatabase("interTwine");
        MongoCollection<Document> collection = database.getCollection("passages");
        Document filter = new Document("id", id);
        Document update = new Document("$set", new Document("text", newText));

        collection.updateOne(filter, update);

        return serialize(handlerSuccessResponse(newText));
    }

}
