package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "abstract_post_id"})
)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
