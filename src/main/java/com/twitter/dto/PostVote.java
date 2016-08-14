package com.twitter.dto;

import com.twitter.model.Vote;

import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 13.08.16.
 */
public class PostVote {
    @NotNull
    private long postId;

    @NotNull
    private Vote vote;

    public PostVote() {
    }

    public PostVote(long postId, Vote vote) {
        this.postId = postId;
        this.vote = vote;
    }

    public long getPostId() {
        return postId;
    }

    public Vote getVote() {
        return vote;
    }
}
