package edu.brown.cs.student.main.server.handlers.stories;

import org.bson.Document;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.brown.cs.student.main.server.handlers.MongoDBHandler;
import spark.Request;
import spark.Response;

/**
 * Handler class for a bulk GET request of a user's stories.
 */
public class LibraryLoadHandler extends MongoDBHandler {
    public LibraryLoadHandler(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    /**
     * Handles library load requests, involving
     *
     * @param request  the request to handle
     * @param response use to modify properties of the response
     * @return
     * @throws Exception (this is a required part of the interface)
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        System.out.println("LIBRARY LOAD begun");

        try {

            String id = request.params("id");
            if (id == null) {
                return serialize(
                        handlerFailureResponse("error_bad_request",
                                "user id <id> is a required query parameter (usage: GET request to .../libraryload/<id>)"));
            }
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("stories");
            // TODO distinguish owned vs shared stories in response
            ArrayList<Document> userStories = new ArrayList<Document>();
            try {
                // look through all stories for owned/shared stories
                for (Document doc : collection.find(new Document())) {
                    if (doc.get("owner").equals(id)) {
                        userStories.add(doc);
                        // System.out.println("LIBRARY LOAD to send back doc: " + doc.toJson() + "\n");
                    } else {
                        List<String> editors = doc.getList("editors", String.class);
                        // System.out.println("LIBRARY LOAD test print, id = " + id);
                        if (editors.contains(id)) {
                            userStories.add(doc);
                        }
                    }
                }
            } catch (MongoException e) {
                return serialize(
                        handlerFailureResponse("error_datasource", "could not find "));
            }
            return serialize(handlerSuccessResponse(userStories));
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
