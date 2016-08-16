package com.twitter.builders;

import com.twitter.model.*;
import com.twitter.util.Builder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 21.07.16.
 */
public final class CommentBuilder implements Builder<Comment> {
    private static long counter = 0L;
    private boolean banned = false;
    private boolean deleted = false;
    private String content = "comment content nr " + counter;
    private Tweet tweet;
    private User owner;
    private List<UserVote> votes = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();
    private long id;
    private Date createDate = Calendar.getInstance().getTime();


    public static CommentBuilder comment() {
        counter++;
        return new CommentBuilder();
    }

    public CommentBuilder withBanned(boolean banned) {
        this.banned = banned;
        return this;
    }

    public CommentBuilder withDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public CommentBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public CommentBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public CommentBuilder withTweet(Tweet tweet) {
        this.tweet = tweet;
        return this;
    }

    public CommentBuilder withVotes(List<UserVote> votes) {
        this.votes = votes;
        return this;
    }

    public CommentBuilder withReports(List<Report> reports) {
        this.reports = reports;
        return this;
    }

    public CommentBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public CommentBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }


    public CommentBuilder withNumberOfVotesOf(long votes) {
        List<UserVote> voteList = new ArrayList<>();
        for (long currentVote = 0; currentVote < votes; currentVote++) {
            voteList.add(a(userVote()
                    .withUser(a(user()))
                    .withVote(Vote.UP)
            ));
        }
        this.votes = voteList;
        return this;
    }

    public Comment build() {
        Comment comment = new Comment();
        comment.setBanned(banned);
        comment.setContent(content);
        comment.setOwner(owner);
        comment.setTweet(tweet);
        comment.setVotes(votes);
        comment.setReports(reports);
        comment.setId(id);
        comment.setDeleted(deleted);
        comment.setCreateDate(createDate);
        return comment;
    }
}
