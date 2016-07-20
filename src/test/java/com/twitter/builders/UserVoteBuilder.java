package com.twitter.builders;

import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
import org.joda.time.DateTime;

/**
 * Created by mariusz on 19.07.16.
 */
public final class UserVoteBuilder {
    private Vote vote;
    private User user;
    private long id;
    private DateTime createDate;

    private UserVoteBuilder() {
    }

    public static UserVoteBuilder anUserVote() {
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

    public UserVoteBuilder withCreateDate(DateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public UserVote build() {
        UserVote userVote = new UserVote();
        userVote.setVote(vote);
        userVote.setUser(user);
        userVote.setId(id);
        userVote.setCreateDate(createDate);
        return userVote;
    }
}
