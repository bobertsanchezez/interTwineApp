package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import com.mongodb.client.MongoClient;

import edu.brown.cs.student.main.server.handlers.passages.PassageDeleteHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassageGetHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassagePostHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassagePutHandler;
import edu.brown.cs.student.main.server.handlers.stories.LibraryLoadHandler;
import edu.brown.cs.student.main.server.handlers.stories.StoryDeleteHandler;
import edu.brown.cs.student.main.server.handlers.stories.StoryGetHandler;
import edu.brown.cs.student.main.server.handlers.stories.StoryPostHandler;
import edu.brown.cs.student.main.server.handlers.stories.StoryPutHandler;
import spark.Spark;

/**
 * Top-level server-running class for a MongoDB database backend.
 * Starts Spark and runs the MongoDB data manipulation handlers.
 */
public class Main {
    public static void main(String[] args) {
        Spark.port(3232);
        after(
                (request, response) -> {
                    response.header("Access-Control-Allow-Origin", "*");
                    response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
                });

        // set up one connection to MongoDB, shared with all servers
        MongoClient mc = MongoClientConnection.startConnection();
        String databaseName = "InterTwine";

        /// Setting up HTTP endpoints for stories, passages
        // /stories GET, POST, PUT, DELETE endpoints
        Spark.get("/libraryload/:id", new LibraryLoadHandler(mc, databaseName));
        Spark.get("/stories", new StoryGetHandler(mc, databaseName));
        Spark.post("/stories", new StoryPostHandler(mc, databaseName));
        Spark.put("/stories/:id", new StoryPutHandler(mc, databaseName));
        Spark.delete("/stories/:id", new StoryDeleteHandler(mc, databaseName));

        // /passages GET, POST, PUT, DELETE endpoints
        Spark.get("/passages", new PassageGetHandler(mc, databaseName));
        Spark.post("/passages", new PassagePostHandler(mc, databaseName));
        Spark.put("/passages/:id", new PassagePutHandler(mc, databaseName));
        Spark.delete("/passages/:id", new PassageDeleteHandler(mc, databaseName));

        // Options setup

        
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started.");
    }
}