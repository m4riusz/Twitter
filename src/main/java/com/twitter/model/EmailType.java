package com.twitter.model;

/**
 * Created by mariusz on 03.01.17.
 */

public enum EmailType {
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain");

    private String type;

    EmailType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
