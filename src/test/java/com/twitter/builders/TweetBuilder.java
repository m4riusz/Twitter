package com.twitter.builders;

import com.twitter.Builder;
import com.twitter.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mariusz on 21.07.16.
 */
public final class TweetBuilder implements Builder<Tweet> {
    private boolean banned = false;
    private String content = "content";
    private User owner;
    private List<Tag> tags = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<UserVote> votes = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();
    private long id;
    private Date createDate = Calendar.getInstance().getTime();

    public static TweetBuilder tweet() {
         return new TweetBuilder();
    }

    public TweetBuilder withTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public TweetBuilder withComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public TweetBuilder withBanned(boolean banned) {
        this.banned = banned;
        return this;
    }

    public TweetBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public TweetBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public TweetBuilder withVotes(List<UserVote> votes) {
        this.votes = votes;
        return this;
    }

    public TweetBuilder withReports(List<Report> reports) {
        this.reports = reports;
        return this;
    }

    public TweetBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public TweetBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Tweet build() {
        Tweet tweet = new Tweet();
        tweet.setTags(tags);
        tweet.setComments(comments);
        tweet.setBanned(banned);
        tweet.setContent(content);
        tweet.setOwner(owner);
        tweet.setVotes(votes);
        tweet.setReports(reports);
        tweet.setId(id);
        tweet.setCreateDate(createDate);
        return tweet;
    }
}
