package com.twitter.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Tag {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
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
