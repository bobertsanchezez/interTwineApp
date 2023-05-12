package edu.brown.cs32.student.main.passages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import edu.brown.cs.student.main.server.MongoClientConnection;
import edu.brown.cs.student.main.server.handlers.passages.PassagePostHandler;
import edu.brown.cs32.student.main.TestUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class TestPostHandler {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(3333);
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
    Spark.post("/passages", new PassagePostHandler(mc, TestUtil.databaseName));
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

  // Tests posting without a request body.
  @Test
  public void testNoData() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("passages", "POST", "");
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);
    assertTrue(response.contains("error_bad_request"));
  }

  // Tests posting with request body not of passage format.
  @Test
  public void testMalformedData() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("passages", "POST", "{\"1\":\"2\"}");
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);

    assertTrue(response.contains("error_datasource"));
  }

  // Tests the default case of posting a new untitled passage.
  @Test
  public void testSuccess() throws IOException {
    String requestBody;
    String filename = TestUtil.absolutePathHeader + TestUtil.passageMockHeader + "untitled_passage.json";

    try {
      requestBody = TestUtil.readJsonFileAsString(filename);
    } catch (Exception e) {
      e.printStackTrace();
      fail("bad testing json filepath: " + e.toString());
      return;
    }

    HttpURLConnection clientConnection = TestUtil.tryRequest("passages", "POST", requestBody);
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);
    assertTrue(response.contains("success"));
    // check if the document is actually in the database
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    Document doc = database.getCollection("passages").find(new Document("name", "Untitled Passage")).first();
    assertNotNull(doc);
  }

  // Tests posting multiple passages.
  @Test
  public void testMultipleSuccess() throws IOException {
    String rb1;
    String rb2;
    String rb3;
    String fn1 = TestUtil.absolutePathHeader + TestUtil.passageMockHeader + "passage.json";
    String fn2 = TestUtil.absolutePathHeader + TestUtil.passageMockHeader + "passage_2.json";
    String fn3 = TestUtil.absolutePathHeader + TestUtil.passageMockHeader + "passage_3.json";

    try {
      rb1 = TestUtil.readJsonFileAsString(fn1);
      rb2 = TestUtil.readJsonFileAsString(fn2);
      rb3 = TestUtil.readJsonFileAsString(fn3);
    } catch (Exception e) {
      e.printStackTrace();
      fail("bad testing json filepath: " + e.toString());
      return;
    }
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    // POST 3 different passages, checking response each time
    // Ensure each passage exists in database after each post (no overwriting)

    // POST passage
    HttpURLConnection cc1 = TestUtil.tryRequest("passages", "POST", rb1);
    assertEquals(200, cc1.getResponseCode());
    String resp1 = TestUtil.getResponse(cc1);
    assertTrue(resp1.contains("success"));

    // Check for passage
    Document doc1 = database.getCollection("passages").find(new Document("name", "passage")).first();
    assertNotNull(doc1);

    // POST passage_2
    HttpURLConnection cc2 = TestUtil.tryRequest("passages", "POST", rb2);
    assertEquals(200, cc2.getResponseCode());
    String resp2 = TestUtil.getResponse(cc2);
    assertTrue(resp2.contains("success"));

    // Check for passage, passage_2
    Document doc1_2 = database.getCollection("passages").find(new Document("name", "passage")).first();
    assertNotNull(doc1_2);
    Document doc2 = database.getCollection("passages").find(new Document("name", "passage_2")).first();
    assertNotNull(doc2);

    // POST passage_3
    HttpURLConnection cc3 = TestUtil.tryRequest("passages", "POST", rb3);
    assertEquals(200, cc3.getResponseCode());
    String resp3 = TestUtil.getResponse(cc3);
    assertTrue(resp3.contains("success"));

    // Check for passage, passage_2, passage_3
    Document doc1_3 = database.getCollection("passages").find(new Document("name", "passage")).first();
    assertNotNull(doc1_3);
    Document doc2_2 = database.getCollection("passages").find(new Document("name", "passage_2")).first();
    assertNotNull(doc2_2);
    Document doc3 = database.getCollection("passages").find(new Document("name", "passage_3")).first();
    assertNotNull(doc3);

  }

  // Tests posting the same passage twice, which should only result in one copy.
  @Test
  public void testRedundantPost() throws IOException {
    String requestBody;
    String filename = TestUtil.absolutePathHeader + TestUtil.passageMockHeader + "passage.json";

    try {
      requestBody = TestUtil.readJsonFileAsString(filename);
    } catch (Exception e) {
      e.printStackTrace();
      fail("bad testing json filepath: " + e.toString());
      return;
    }

    // POST passage
    HttpURLConnection cc1 = TestUtil.tryRequest("passages", "POST", requestBody);
    assertEquals(200, cc1.getResponseCode());
    String resp1 = TestUtil.getResponse(cc1);
    assertTrue(resp1.contains("success"));

    // Try to POST passage again, note same requestBody
    HttpURLConnection cc2 = TestUtil.tryRequest("passages", "POST", requestBody);
    assertEquals(200, cc2.getResponseCode());
    String resp2 = TestUtil.getResponse(cc2);
    assertTrue(resp2.contains("success"));

    // check if EXACTLY ONE of the document is in the database
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    FindIterable<Document> docs = database.getCollection("passages").find(new Document("name", "passage"));

    // count docs found
    int count = 0;
    for (Document doc : docs) {
      count++;
      if (doc.isEmpty()) {
        // make yellow squiggle go away
      }
    }
    assertEquals(resp1, resp2);
    assertEquals(1, count);
  }

}