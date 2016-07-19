package com.twitter.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Tag> tags;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> comments;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVote> votes;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;


    public Tweet() {
        super();
        this.banned = false;
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.votes = new ArrayList<>();
        this.reports = new ArrayList<>();
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tweet> getComments() {
        return comments;
    }

    public void setComments(List<Tweet> comments) {
        this.comments = comments;
    }

    public List<UserVote> getVotes() {
        return votes;
    }

    public void setVotes(List<UserVote> votes) {
        this.votes = votes;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tweet)) return false;
        if (!super.equals(o)) return false;

        Tweet tweet = (Tweet) o;

        return content.equals(tweet.content);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }
}
