package edu.brown.cs32.student.main.passages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import edu.brown.cs.student.main.server.MongoClientConnection;
import edu.brown.cs.student.main.server.handlers.passages.PassageDeleteHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassageGetHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassagePostHandler;
import edu.brown.cs.student.main.server.handlers.passages.PassagePutHandler;
import edu.brown.cs32.student.main.TestUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import okio.Buffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import com.mongodb.client.MongoClient;

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
    // Server server = new Server();
    mc = MongoClientConnection.startConnection();
    Spark.get("/passages", new PassagePostHandler(mc, TestUtil.databaseName));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    mc.close();
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/passages");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  // Tests GET requests lacking an id parameter.
  // @Test
  // public void testNoId() throws IOException {
  // HttpURLConnection clientConnection = TestUtil.tryRequest("passages", "GET");
  // assertEquals(200, clientConnection.getResponseCode());
  // String response = TestUtil.getResponse(clientConnection);

  // assertTrue(response.contains("error_bad_request"));
  // }

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