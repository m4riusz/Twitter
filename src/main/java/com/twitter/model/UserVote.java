package com.twitter.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */

@Entity
public class UserVote extends AbstractEntity {

    @NotNull
    private Vote vote;
    @NotNull
    @ManyToOne
    private User user;
    @NotNull
    @ManyToOne
    private Tweet tweet;

    public UserVote() {
        super();
    }

    public UserVote(Vote vote, User user, Tweet tweet) {
        this();
        this.tweet = tweet;
        this.vote = vote;
        this.user = user;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
