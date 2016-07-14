package com.twitter.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Tweet extends AbstractEntity {

    @NotNull
    private boolean banned;
    @NotNull
    private String content;
    @NotNull
    @ManyToOne
    private User owner;
    @NotNull
    @ManyToMany
    private Set<Tag> tags;
    @NotNull
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "tweetId"),
            inverseJoinColumns = @JoinColumn(name = "commentId"))
    private Set<Tweet> comments;
    @NotNull
    @ManyToMany
    private Set<UserVote> votes;
    @NotNull
    @OneToMany
    private Set<Report> reports;


    public Tweet() {
        super();
        this.banned = false;
        this.tags = new HashSet<>();
        this.comments = new HashSet<>();
        this.votes = new HashSet<>();
        this.reports = new HashSet<>();
    }

    public Tweet(String content, User owner) {
        this();
        this.content = content;
        this.owner = owner;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Tweet> getComments() {
        return comments;
    }

    public void setComments(Set<Tweet> comments) {
        this.comments = comments;
    }

    public Set<UserVote> getVotes() {
        return votes;
    }

    public void setVotes(Set<UserVote> votes) {
        this.votes = votes;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }
}
