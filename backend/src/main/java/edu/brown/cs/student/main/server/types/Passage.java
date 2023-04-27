package edu.brown.cs.student.main.server.types;

import java.util.List;

public class Passage {
    private int height;
    private boolean highlighted;
    private String id;
    private int left;
    private String name;
    private boolean selected;
    private String story;
    private List<String> tags;
    private String text;
    private int top;
    private int width;
    private boolean claimed;
    private String user;

    public Passage(int height, boolean highlighted, String id, int left, String name, boolean selected,
            String story, List<String> tags, String text, int top, int width, boolean claimed, String user) {
        this.height = height;
        this.highlighted = highlighted;
        this.id = id;
        this.left = left;
        this.name = name;
        this.selected = selected;
        this.story = story;
        this.tags = tags;
        this.text = text;
        this.top = top;
        this.width = width;
        this.claimed = claimed;
        this.user = user;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
