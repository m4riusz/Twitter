package com.twitter.builders;

import com.twitter.model.Vote;
import com.twitter.model.dto.PostVote;
import com.twitter.util.Builder;

/**
 * Created by mariusz on 13.08.16.
 */
public final class PostVoteBuilder implements Builder<PostVote> {
    private long postId;
    private Vote vote;

    public static PostVoteBuilder postVote() {
        return new PostVoteBuilder();
    }

    public PostVoteBuilder withPostId(long postId) {
        this.postId = postId;
        return this;
    }

    public PostVoteBuilder withVote(Vote vote) {
        this.vote = vote;
        return this;
    }

    public PostVote build() {
        PostVote postVote = new PostVote(postId, vote);
        return postVote;
    }
}
