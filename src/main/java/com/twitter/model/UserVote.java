package com.twitter.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */

@Entity
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private Vote vote;
    @NotNull
    @ManyToOne
    private User user;

    public UserVote() {
    }

    public UserVote(Vote vote, User user) {
        this();
        this.vote = vote;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
