package edu.brown.cs.student.main.server.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class Story implements Bson {
    /**
     * IFID of the story. An IFID should stay stable when a story is imported or
     * exported.
     */
    private String ifid;
    /**
     * GUID identifying the story.
     */
    private String id;
    /**
     * When the story was last changed.
     */
    private Date lastUpdate;
    /**
     * Name of the story.
     */
    private String name;
    /**
     * IDs of Passages in the story.
     */
    private List<ObjectId> passageIds;
    /**
     * Passages in the story.
     */
    private Passage[] passages;
    /**
     * Author-created JavaScript associated with the story.
     */
    private String script;
    /**
     * Is the story currently selected by the user?
     */
    private boolean selected;
    /**
     * Should passages snap to a grid?
     */
    private boolean snapToGrid;
    /**
     * ID of the passage that the story begins at.
     */
    private String startPassage;
    /**
     * Name of the story format the story uses.
     */
    private String storyFormat;
    /**
     * Version of the story format that this story uses.
     */
    private String storyFormatVersion;
    /**
     * Author-created CSS associated with the story.
     */
    private String stylesheet;
    /**
     * Tags applied to the story.
     */
    private String[] tags;
    /**
     * Author-specified colors for passage tags.
     */
    private Map<String, String> tagColors;
    /**
     * Zoom level the story is displayed at.
     */
    private int zoom;
    /**
     * InterTwine Dev Tools under here.
     */

    /**
     * Owner of the story.
     */
    private String owner;
    /**
     * People who are allowed to edit the story.
     */
    private String[] editors;

    // Constructor
    public Story(String ifid, String id, Date lastUpdate, String name, Passage[] passages,
            String script,
            boolean selected, boolean snapToGrid, String startPassage, String storyFormat, String storyFormatVersion,
            String stylesheet, String[] tags, Map<String, String> tagColors, int zoom, String owner, String[] editors) {
        this.ifid = ifid;
        this.id = id;
        this.lastUpdate = lastUpdate;
        this.name = name;
        this.passageIds = new ArrayList<ObjectId>();
        this.passages = passages;
        this.script = script;
        this.selected = selected;
        this.snapToGrid = snapToGrid;
        this.startPassage = startPassage;
        this.storyFormat = storyFormat;
        this.storyFormatVersion = storyFormatVersion;
        this.stylesheet = stylesheet;
        this.tags = tags;
        this.tagColors = tagColors;
        this.zoom = zoom;
        this.owner = owner;
        this.editors = editors;
        // populate passageIds function here!
    }

    public String getIfid() {
        return ifid;
    }

    public void setIfid(String ifid) {
        this.ifid = ifid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectId> getPassageIds() {
        return passageIds;
    }

    public void setPassageId(List<ObjectId> passageIds) {
        this.passageIds = passageIds;
    }

    public Passage[] getPassages() {
        return passages;
    }

    public void setPassages(Passage[] passages) {
        this.passages = passages;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public String getStartPassage() {
        return startPassage;
    }

    public void setStartPassage(String startPassage) {
        this.startPassage = startPassage;
    }

    public String getStoryFormat() {
        return storyFormat;
    }

    public void setStoryFormat(String storyFormat) {
        this.storyFormat = storyFormat;
    }

    public String getStoryFormatVersion() {
        return storyFormatVersion;
    }

    public void setStoryFormatVersion(String storyFormatVersion) {
        this.storyFormatVersion = storyFormatVersion;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Map<String, String> getTagColors() {
        return tagColors;
    }

    public void setTagColors(Map<String, String> tagColors) {
        this.tagColors = tagColors;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String[] getEditors() {
        return editors;
    }

    public void setEditors(String[] editors) {
        this.editors = editors;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> document, CodecRegistry registry) {
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.put("ifid", new BsonString(ifid));
        bsonDocument.put("id", new BsonString(id));
        bsonDocument.put("lastUpdate", new BsonDateTime(lastUpdate.getTime()));
        bsonDocument.put("name", new BsonString(name));
        BsonArray passageIdsArray = new BsonArray();
        // TODO UNCOMMENT BELOW WHEN IMPLEMENTING CROSSLINKING (causes error)
        // for (ObjectId passageId : passageIds) {
        // passageIdsArray.add(new BsonObjectId(passageId));
        // }
        bsonDocument.put("passageIds", passageIdsArray);
        BsonArray passagesArray = new BsonArray();
        for (Passage passage : passages) {
            passagesArray.add(passage.toBsonDocument());
        }
        bsonDocument.put("passages", passagesArray);
        bsonDocument.put("script", new BsonString(script));
        bsonDocument.put("selected", new BsonBoolean(selected));
        bsonDocument.put("snapToGrid", new BsonBoolean(snapToGrid));
        bsonDocument.put("startPassage", new BsonString(startPassage));
        bsonDocument.put("storyFormat", new BsonString(storyFormat));
        bsonDocument.put("storyFormatVersion", new BsonString(storyFormatVersion));
        bsonDocument.put("stylesheet", new BsonString(stylesheet));
        BsonArray tagsArray = new BsonArray();
        for (String tag : tags) {
            tagsArray.add(new BsonString(tag));
        }
        bsonDocument.put("tags", tagsArray);
        BsonDocument tagColorsDocument = new BsonDocument();
        for (String key : tagColors.keySet()) {
            tagColorsDocument.put(key, new BsonString(tagColors.get(key)));
        }
        bsonDocument.put("tagColors", tagColorsDocument);
        bsonDocument.put("zoom", new BsonInt32(zoom));
        bsonDocument.put("owner", new BsonString(owner));
        BsonArray editorsArray = new BsonArray();
        for (String editor : editors) {
            editorsArray.add(new BsonString(editor));
        }
        bsonDocument.put("editors", editorsArray);
        return bsonDocument;
    }
}
