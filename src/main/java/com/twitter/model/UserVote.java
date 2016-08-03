package com.twitter.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "abstract_post_id"})
)
public class UserVote extends AbstractEntity {

    @NotNull
    private Vote vote;
    @NotNull
    @ManyToOne
    private User user;
    @NotNull
    @ManyToOne
    private AbstractPost abstractPost;

    public UserVote() {
        super();
    }

    public UserVote(Vote vote, User user, AbstractPost abstractPost) {
        this();
        this.abstractPost = abstractPost;
        this.vote = vote;
        this.user = user;
    }

    public AbstractPost getAbstractPost() {
        return abstractPost;
    }

    public void setAbstractPost(AbstractPost abstractPost) {
        this.abstractPost = abstractPost;
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
