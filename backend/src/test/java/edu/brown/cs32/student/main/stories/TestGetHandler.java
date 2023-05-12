package edu.brown.cs32.student.main.stories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.MongoClientConnection;
import edu.brown.cs.student.main.server.handlers.passages.PassageGetHandler;
import edu.brown.cs32.student.main.TestUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import com.mongodb.client.MongoClient;

/*
 * TESTING PLAN:
 *
 * There's nothing special with this one, so:
 * 
 * Test the general case
 * Test that a nonexistent story returns an error
 * 
 * 
 */

public class TestGetHandler {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes
   * etc.) but never need to replace
   * the reference itself. We clear this state out after every test runs.
   */
  @BeforeEach
  public void setup() {
    // Server server = new Server();
    MongoClient mc = MongoClientConnection.startConnection();
    Spark.get("get", new PassageGetHandler(mc, TestUtil.databaseName));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/get");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint
   *                (NOTE: this would be better if it had more structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    System.out.println(requestURL);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testCorrect() throws IOException {
    HttpURLConnection clientConnection = tryRequest("get?id=1");
    assertEquals(200, clientConnection.getResponseCode());
  }

  @Test
  public void testNoData() throws IOException {
    HttpURLConnection clientConnection = tryRequest("get");
    assertEquals(200, clientConnection.getResponseCode());
  }
}