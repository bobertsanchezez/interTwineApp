package edu.brown.cs.student.main.server.handlers.passages;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.eclipse.jetty.util.IO;

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
        String data = request.queryParams("data");
        if (data == null) {
            return serialize(handlerFailureResponse("error_bad_request",
                    "data payload <data> must be supplied (jsonified Passage data)"));
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
        MongoDatabase database = mongoClient.getDatabase("interTwine");
        MongoCollection<Document> collection = database.getCollection("passages");

        // collection.insertOne(passage);

        return serialize(handlerSuccessResponse(""));
    }

}
