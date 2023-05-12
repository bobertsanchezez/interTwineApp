package edu.brown.cs32.student.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import spark.Spark;

/**
 * Contains useful methods for testing a SparkJava MongoDB server's handlers.
 */
public class TestUtil {

    public static String databaseName = "InterTwineTest";
    public static String absolutePathHeader = "C:\\Users\\Henry\\Desktop\\cs320\\interTwineApp\\backend\\src\\test\\java\\edu\\brown\\cs32\\student\\main\\";
    public static String passageMockHeader = "passages\\mocks\\";
    public static String storyMockHeader = "stories\\mocks\\";

    /**
     * Helper to start a connection to a specific API endpoint/params
     *
     * @param apiCall the call string, including endpoint
     * 
     * @return the connection for the given URL, just after connecting
     * @throws IOException if the connection fails for some reason
     */
    public static HttpURLConnection tryRequest(String apiCall, String requestMethod) throws IOException {
        // Configure the connection (but don't actually send the request yet)
        URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

        // The default method is "GET", which is what we're using here.
        // If we were using "POST", we'd need to say so.
        clientConnection.setRequestMethod(requestMethod);

        clientConnection.connect();
        return clientConnection;
    }

    public static HttpURLConnection tryRequest(String apiCall, String requestMethod, String requestBody)
            throws IOException {
        // Configure the connection (but don't actually send the request yet)
        URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

        // Set the request method and content type
        clientConnection.setRequestMethod(requestMethod);
        clientConnection.setRequestProperty("Content-Type", "application/json");

        // Write the request body, if one was provided
        if (requestBody != null && !requestBody.isEmpty()) {
            clientConnection.setDoOutput(true);
            OutputStream os = clientConnection.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
        }

        // Send the request and return the connection object
        clientConnection.connect();
        return clientConnection;
    }

    /**
     * Given a json filepath, returns its contents in string form.
     * 
     * @param filename
     * @return
     * @throws Exception
     */
    public static String readJsonFileAsString(String filename) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        return content;
    }

    /**
     * Given a connection, returns the response it received as a string.
     * 
     * @param clientConnection
     * @return
     * @throws IOException
     */
    public static String getResponse(HttpURLConnection clientConnection) throws IOException {
        // Read the response body and convert it to a string
        InputStream inputStream = clientConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            responseBuilder.append(line);
        }
        String response = responseBuilder.toString();
        return response;
    }
}
