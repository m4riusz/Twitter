package com.twitter.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */

@Entity
public class UserVote {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
    private Vote vote;
    @NotNull
    @ManyToOne
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
