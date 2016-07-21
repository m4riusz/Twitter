package com.twitter.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 21.07.16.
 */
@Entity
public class Comment extends AbstractPost {
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Tweet tweet;

    public Comment() {
        super();
    }

    public Comment(String content, User owner, Tweet tweet) {
        super(content, owner);
        this.tweet = tweet;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }
}
