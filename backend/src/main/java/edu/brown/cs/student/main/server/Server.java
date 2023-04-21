package edu.brown.cs.student.main.server;

import static spark.Spark.after;

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
 * Top-level class for a redlining database backend. Contains the main() method
 * which starts Spark and runs the redlining handler.
 */
public class Server {
    public static void main(String[] args) {
        Spark.port(6969);
        after(
                (request, response) -> {
                    response.header("Access-Control-Allow-Origin", "*");
                    response.header("Access-Control-Allow-Methods", "*");
                });

        // Setting up HTTP endpoints for stories, passages
        Spark.get("/stories", new StoryGetHandler());
        Spark.get("/passages", new PassageGetHandler());
        Spark.post("/stories", new StoryPostHandler());
        Spark.post("/passages", new PassagePostHandler());
        Spark.put("/stories/:id", new StoryPutHandler());
        Spark.put("/passages/:id", new PassagePutHandler());
        Spark.delete("/stories/:id", new StoryDeleteHandler());
        Spark.delete("/passages/:id", new PassageDeleteHandler());

        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started.");
    }
}