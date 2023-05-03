// package edu.brown.cs.student.main.server.handlers.stories;

// public class LibraryLoadHandler extends MongoDBHandler {
// public LibraryLoadHandler(MongoClient mongoClient) {
// super(mongoClient);
// }

// /**
// *
// *
// * @param request the request to handle
// * @param response use to modify properties of the response
// * @return
// * @throws Exception (this is a required part of the interface)
// */
// @Override
// public Object handle(Request request, Response response) throws Exception {
// String id = request.queryParams("id");
// if (id == null) {
// return serialize(
// handlerFailureResponse("error_bad_request",
// "story id <id> is a required query parameter (usage: GET request to
// .../stories?id=12345)"));
// }
// MongoDatabase database = mongoClient.getDatabase("InterTwine");
// MongoCollection<Document> collection = database.getCollection("stories");
// Document doc;
// try {
// doc = collection.find(eq("id", id))
// .first();
// } catch (MongoException e) {
// return serialize(
// handlerFailureResponse("error_datasource", "id " + id + " does not exist in
// the database"));
// }
// if (doc == null) {
// return serialize(
// handlerFailureResponse("error_datasource", "id " + id + " does not exist in
// the database"));
// } else {
// return serialize(handlerSuccessResponse(doc.toJson()));
// }
// }
// }
