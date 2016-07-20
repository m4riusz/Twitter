package com.twitter.builders;

import com.twitter.Builder;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 19.07.16.
 */
public final class UserVoteBuilder implements Builder<UserVote> {
    private Vote vote = Vote.UP;
    private User user;
    private long id = 0;
    private Tweet tweet;
    private Date createDate = Calendar.getInstance().getTime();

    private UserVoteBuilder() {
    }

    public static UserVoteBuilder userVote() {
        return new UserVoteBuilder();
    }

    public UserVoteBuilder withVote(Vote vote) {
        this.vote = vote;
        return this;
    }

    public UserVoteBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public UserVoteBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public UserVoteBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public UserVoteBuilder withTweet(Tweet tweet) {
        this.tweet = tweet;
        return this;
    }

    public UserVote build() {
        UserVote userVote = new UserVote();
        userVote.setVote(vote);
        userVote.setUser(user);
        userVote.setId(id);
        userVote.setCreateDate(createDate);
        userVote.setTweet(tweet);
        return userVote;
    }
}
