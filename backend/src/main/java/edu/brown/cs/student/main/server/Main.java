package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import com.mongodb.client.MongoClient;

import edu.brown.cs.student.main.server.handlers.passages.PassageDeleteHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassageGetHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassagePostHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassagePutHandler;
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
                    response.header("Access-Control-Allow-Methods", "*");
                });

        // set up 8 distinct connections to MongoDB; one for each handler
        // this is done to not overload a given connection
        MongoClient mc1 = MongoClientConnection.startConnection();
        MongoClient mc2 = MongoClientConnection.startConnection();
        MongoClient mc3 = MongoClientConnection.startConnection();
        MongoClient mc4 = MongoClientConnection.startConnection();
        MongoClient mc5 = MongoClientConnection.startConnection();
        MongoClient mc6 = MongoClientConnection.startConnection();
        MongoClient mc7 = MongoClientConnection.startConnection();
        MongoClient mc8 = MongoClientConnection.startConnection();

        // Setting up HTTP endpoints for stories, passages
        // /stories GET, POST, PUT, DELETE endpoints
        Spark.get("/stories", new StoryGetHandler(mc1));
        Spark.post("/stories", new StoryPostHandler(mc2));
        Spark.put("/stories/:id", new StoryPutHandler(mc3));
        Spark.delete("/stories/:id", new StoryDeleteHandler(mc4));

        // /passages GET, POST, PUT, DELETE endpoints
        Spark.get("/passages", new PassageGetHandler(mc5));
        Spark.post("/passages", new PassagePostHandler(mc6));
        Spark.put("/passages/:id", new PassagePutHandler(mc7));
        Spark.delete("/passages/:id", new PassageDeleteHandler(mc8));

        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started.");
    }
}