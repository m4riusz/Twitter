package com.twitter.model;

/**
 * Created by mariusz on 11.07.16.
 */
public class Tag {
    private int id;
    private String text;

    public Tag(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
