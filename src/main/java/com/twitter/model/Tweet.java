package com.twitter.model;

import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mariusz on 11.07.16.
 */
public class Tweet {
    private int id;
    private boolean banned;
    private DateTime date;
    private String content;
    private User owner;
    private Set<Tag> tags;
    private Set<Tweet> comments;
    private Set<UserVote> votes;
    private Set<Report> reports;

    public Tweet() {
        this.banned = false;
        this.date = DateTime.now();
        this.tags = new HashSet<>();
        this.comments = new HashSet<>();
        this.votes = new HashSet<>();
        this.reports = new HashSet<>();
    }

    public Tweet(String content, User owner) {
        super();
        this.content = content;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
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
