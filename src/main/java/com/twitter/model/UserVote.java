package com.twitter.model;

/**
 * Created by mariusz on 11.07.16.
 */

public class UserVote {
    private int id;
    private Vote vote;
    private User user;

    public UserVote(Vote vote, User user) {
        this.vote = vote;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
