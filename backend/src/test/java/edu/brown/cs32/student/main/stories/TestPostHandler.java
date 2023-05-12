package edu.brown.cs32.student.main.stories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import edu.brown.cs.student.main.server.MongoClientConnection;
import edu.brown.cs.student.main.server.handlers.stories.StoryPostHandler;
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

/**
 * TESTING PLAN:
 * 
 * see below :)
 */
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
    Spark.post("/stories", new StoryPostHandler(mc, TestUtil.databaseName));
    Spark.init();
    Spark.awaitInitialization();
    // set up testing collection
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    database.createCollection("stories");
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/stories");
    Spark.awaitStop(); // don't proceed until the server is stopped
    // drop testing collection
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    database.getCollection("stories").drop();
    mc.close();
  }

  // Tests posting without a request body.
  @Test
  public void testNoData() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("stories", "POST", "");
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);
    assertTrue(response.contains("error_bad_request"));
  }

  // Tests posting with a request body not of story format.
  @Test
  public void testMalformedData() throws IOException {
    HttpURLConnection clientConnection = TestUtil.tryRequest("stories", "POST", "{\"1\":\"2\"}");
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);

    assertTrue(response.contains("error_datasource"));
  }

  // Tests the default case of posting a new untitled story.
  @Test
  public void testSuccess() throws IOException {
    String requestBody;
    String filename = TestUtil.absolutePathHeader + TestUtil.storyMockHeader + "untitled_story.json";

    try {
      requestBody = TestUtil.readJsonFileAsString(filename);
    } catch (Exception e) {
      e.printStackTrace();
      fail("bad testing json filepath: " + e.toString());
      return;
    }

    HttpURLConnection clientConnection = TestUtil.tryRequest("stories", "POST", requestBody);
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);
    assertTrue(response.contains("success"));
    // check if the document is actually in the database
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    Document doc = database.getCollection("stories").find(new Document("name", "Untitled Story")).first();
    assertNotNull(doc);
  }

  // Tests the default case of posting a new untitled story.
  @Test
  public void testMultiplePassageStory() throws IOException {
    String requestBody;
    String filename = TestUtil.absolutePathHeader + TestUtil.storyMockHeader + "multiple_passages_story.json";

    try {
      requestBody = TestUtil.readJsonFileAsString(filename);
    } catch (Exception e) {
      e.printStackTrace();
      fail("bad testing json filepath: " + e.toString());
      return;
    }

    HttpURLConnection clientConnection = TestUtil.tryRequest("stories", "POST", requestBody);
    assertEquals(200, clientConnection.getResponseCode());

    String response = TestUtil.getResponse(clientConnection);
    assertTrue(response.contains("success"));
    // check if the document is actually in the database
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    Document doc = database.getCollection("stories").find(new Document("name", "multiple_passages_story")).first();
    assertNotNull(doc);
  }

  // Tests posting multiple stories.
  @Test
  public void testMultipleSuccess() throws IOException {
    String rb1;
    String rb2;
    String rb3;
    String fn1 = TestUtil.absolutePathHeader + TestUtil.storyMockHeader + "story.json";
    String fn2 = TestUtil.absolutePathHeader + TestUtil.storyMockHeader + "story_2.json";
    String fn3 = TestUtil.absolutePathHeader + TestUtil.storyMockHeader + "story_3.json";

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
    // POST 3 different stories, checking response each time
    // Ensure each story exists in database after each post (no overwriting)

    // POST story
    HttpURLConnection cc1 = TestUtil.tryRequest("stories", "POST", rb1);
    assertEquals(200, cc1.getResponseCode());
    String resp1 = TestUtil.getResponse(cc1);
    assertTrue(resp1.contains("success"));

    // Check for story
    Document doc1 = database.getCollection("stories").find(new Document("name", "story")).first();
    assertNotNull(doc1);

    // POST story_2
    HttpURLConnection cc2 = TestUtil.tryRequest("stories", "POST", rb2);
    assertEquals(200, cc2.getResponseCode());
    String resp2 = TestUtil.getResponse(cc2);
    assertTrue(resp2.contains("success"));

    // Check for story, story_2
    Document doc1_2 = database.getCollection("stories").find(new Document("name", "story")).first();
    assertNotNull(doc1_2);
    Document doc2 = database.getCollection("stories").find(new Document("name", "story_2")).first();
    assertNotNull(doc2);

    // POST story_3
    HttpURLConnection cc3 = TestUtil.tryRequest("stories", "POST", rb3);
    assertEquals(200, cc3.getResponseCode());
    String resp3 = TestUtil.getResponse(cc3);
    assertTrue(resp3.contains("success"));

    // Check for story, story_2, story_3
    Document doc1_3 = database.getCollection("stories").find(new Document("name", "story")).first();
    assertNotNull(doc1_3);
    Document doc2_2 = database.getCollection("stories").find(new Document("name", "story_2")).first();
    assertNotNull(doc2_2);
    Document doc3 = database.getCollection("stories").find(new Document("name", "story_3")).first();
    assertNotNull(doc3);

  }

  // Tests posting the same story twice, which should only result in one copy.
  @Test
  public void testRedundantPost() throws IOException {
    String requestBody;
    String filename = TestUtil.absolutePathHeader + TestUtil.storyMockHeader + "story.json";

    try {
      requestBody = TestUtil.readJsonFileAsString(filename);
    } catch (Exception e) {
      e.printStackTrace();
      fail("bad testing json filepath: " + e.toString());
      return;
    }

    // POST story
    HttpURLConnection cc1 = TestUtil.tryRequest("stories", "POST", requestBody);
    assertEquals(200, cc1.getResponseCode());
    String resp1 = TestUtil.getResponse(cc1);
    assertTrue(resp1.contains("success"));

    // Try to POST story again, note same requestBody
    HttpURLConnection cc2 = TestUtil.tryRequest("stories", "POST", requestBody);
    assertEquals(200, cc2.getResponseCode());
    String resp2 = TestUtil.getResponse(cc2);
    assertTrue(resp2.contains("success"));

    // check if EXACTLY ONE of the document is in the database
    MongoDatabase database = mc.getDatabase(TestUtil.databaseName);
    FindIterable<Document> docs = database.getCollection("stories").find(new Document("name", "story"));

    // count docs found
    int count = 0;
    for (Document doc : docs) {
      count++;
      if (doc.isEmpty()) {
        // make yellow squiggle go away
      }
    }
    assertEquals(1, count);
    assertEquals(resp1, resp2);
  }

}