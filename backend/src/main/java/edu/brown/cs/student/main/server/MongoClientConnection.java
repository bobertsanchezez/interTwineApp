package edu.brown.cs.student.main.server;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bson.Document;

public class MongoClientConnection {
    public static MongoClient startConnection() throws MongoException {
        // GRAB CREDENTIALS FROM /private/connection-creds.txt in form
        // "username password"
        String username;
        String password;
        try {
            String connectionCreds = new String(
                    Files.readAllBytes(Paths.get("/private/connection-creds.txt")));
            String[] creds = connectionCreds.split(connectionCreds);
            username = creds[0];
            password = creds[1];
        } catch (IOException | IndexOutOfBoundsException e) {
            throw new MongoException("Failed to read connection credentials from file", e);
        }
        String connectionString = "mongodb+srv://" + username + ":" + password
                + "@cs32.oyxzxfh.mongodb.net/?retryWrites=true&w=majority";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        MongoClient mongoClient = MongoClients.create(settings);
        return mongoClient;
    }
}