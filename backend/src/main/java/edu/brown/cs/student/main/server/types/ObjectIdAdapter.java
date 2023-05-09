package edu.brown.cs.student.main.server.types;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.bson.types.ObjectId;

/**
 * A class to supply to Moshi to instruct adaptation of ObjectIds.
 * Currently unused, because Twine's builtin "id" is used as a unique
 * identifier instead.
 */
public class ObjectIdAdapter {
    @FromJson
    public ObjectId fromJson(String idString) {
        if (idString == null || idString.isEmpty()) {
            return new ObjectId();
        } else {
            return new ObjectId(idString);
        }
    }

    @ToJson
    public String toJson(ObjectId id) {
        return id.toString();
    }
}
