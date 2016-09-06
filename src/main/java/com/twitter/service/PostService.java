package com.twitter.service;

import com.twitter.dto.PostVote;
import com.twitter.model.AbstractPost;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
import com.twitter.util.SecurityUtil;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Created by mariusz on 05.08.16.
 */

@Service
public interface PostService<T extends AbstractPost> {

    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    T create(@Param("post") T post);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    void delete(long postId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    boolean exists(long postId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    T getById(long postId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    UserVote vote(PostVote postVote);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    void deleteVote(long voteId);

    // TODO: 05.09.16 add tests
    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    UserVote getPostVote(long postId);
}
