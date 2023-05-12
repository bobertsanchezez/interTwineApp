package edu.brown.cs32.student.main.passages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.MongoClientConnection;
import edu.brown.cs.student.main.server.handlers.passages.PassageDeleteHandler;
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

/*
 * TESTING PLAN:
 * 
 * Test that passages are deleted from their respective stories upon deletion
 * Test that different kinds of passages can be deleted
 * Test for malformed / nonexistent ids
 * 
 */

public class TestDeleteHandler {
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
    Spark.delete("/passages/:id", new PassageDeleteHandler(mc, TestUtil.databaseName));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    mc.close();
    Spark.unmap("/passages/:id");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  @Test
  public void testWrongRequestMethod() throws IOException {

    HttpURLConnection clientConnection = TestUtil.tryRequest("passages/", "DELETE");

    assertEquals(200, clientConnection.getResponseCode());
  }

  @Test
  public void testNoData() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("delete", "");
    assertEquals(400, clientConnection.getResponseCode());
  }
}