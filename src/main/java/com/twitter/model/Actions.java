package com.twitter.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariusz on 14.07.16.
 */
@Entity
public class Actions extends AbstractEntity {

    @NotNull
    @OneToMany
    private List<Report> reports;
    @NotNull
    @OneToMany
    private List<Tweet> tweets;
    @NotNull
    @ManyToMany
    private List<Tag> favouriteTags;

    public Actions() {
        super();
        this.reports = new ArrayList<>();
        this.tweets = new ArrayList<>();
        this.favouriteTags = new ArrayList<>();
    }

    public Actions(List<Report> reports, List<Tweet> tweets, List<Tag> favouriteTags) {
        this();
        this.reports = reports;
        this.tweets = tweets;
        this.favouriteTags = favouriteTags;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public List<Tag> getFavouriteTags() {
        return favouriteTags;
    }

    public void setFavouriteTags(List<Tag> favouriteTags) {
        this.favouriteTags = favouriteTags;
    }
}
