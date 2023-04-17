package edu.brown.cs.student.main.server;

import static spark.Spark.after;
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

        // Setting up the handler for the GET /redlining endpoint
        // Spark.get("redlining", new
        // redliningHandler("backend/data/geo/fullDownload.json", 100));

        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started.");
    }
}