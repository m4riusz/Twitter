package com.twitter.service;

import com.twitter.model.AbstractPost;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
import com.twitter.util.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Created by mariusz on 16.08.16.
 */
@Service
@PreAuthorize(value = SecurityUtil.AUTHENTICATED)
public interface UserVoteService {

    UserVote save(UserVote userVote);

    void delete(long userVoteId);

    UserVote getById(long userVoteId);

    UserVote findUserVoteForPost(User user, AbstractPost abstractPost);

    boolean exists(long userVoteId);

    // TODO: 28.10.16 add tests
    long getPostVoteCount(long postId, Vote vote);
}
