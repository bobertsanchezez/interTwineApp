package edu.brown.cs32.student.main.passages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.server.MongoClientConnection;
import edu.brown.cs.student.main.server.handlers.passages.PassageGetHandler;
import edu.brown.cs32.student.main.TestUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/*
 * TESTING PLAN:
 * 
 * Test that claimed passages return an error_claimed if a user has claimed them
 * Test that a nonexistent passage returns an error
 * Test the normal get case on an untitled passage
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
  MongoClient mc;

  @BeforeEach
  public void setup() {
    mc = MongoClientConnection.startConnection();
    Spark.get("/passages", new PassageGetHandler(mc, TestUtil.databaseName));
    Spark.init();
    Spark.awaitInitialization();
    // set up testing collection
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    database.createCollection("passages");
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/passages");
    Spark.awaitStop(); // don't proceed until the server is stopped
    // drop testing collection
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    database.getCollection("passages").drop();
    mc.close();
  }

  // Tests GET requests lacking an id parameter.
  @Test
  public void testNoId() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("passages", "GET");
    assertEquals(200, clientConnection.getResponseCode());
    String response = TestUtil.getResponse(clientConnection);

    assertTrue(response.contains("error_bad_request"));
  }

  // Tests ...
  // @Test
  // public void testBadId() throws IOException {
  // HttpURLConnection clientConnection =
  // TestUtil.tryRequest("passages?id=gibberish", "GET");
  // assertEquals(200, clientConnection.getResponseCode());
  // String response = TestUtil.getResponse(clientConnection);

  // assertTrue(response.contains("error_datasource"));
  // }

  // Tests ...
  @Test
  public void testExists() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("passages", "POST");
    assertEquals(200, clientConnection.getResponseCode());
    String response = TestUtil.getResponse(clientConnection);

    assertTrue(response.contains("error_datasource"));
  }

}