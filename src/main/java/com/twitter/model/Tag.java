package com.twitter.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Tag extends AbstractEntity {

    @NotNull
    @Column(unique = true)
    private String text;

    public Tag() {
        super();
    }

    public Tag(String text) {
        this();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
