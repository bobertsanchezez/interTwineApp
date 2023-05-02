package edu.brown.cs.student.main.server.handlers.passages;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
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
            MongoDatabase database = mongoClient.getDatabase("interTwine");
            MongoCollection<Document> collection = database.getCollection("passages");
            Document toDelete = new Document("id", id);
            DeleteResult res = collection.deleteOne(toDelete);
            if (res.getDeletedCount() == 0) {
                return serialize(handlerFailureResponse("error_datasource",
                        "Delete failed: no document with id " + id + " contained in the database"));
            }
            return serialize(handlerSuccessResponse(id));
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
