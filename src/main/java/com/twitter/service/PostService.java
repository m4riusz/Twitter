package com.twitter.service;

import com.twitter.model.AbstractPost;
import com.twitter.model.Result;
import com.twitter.model.UserVote;
import com.twitter.util.SecurityUtil;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by mariusz on 05.08.16.
 */

interface PostService<T extends AbstractPost> {

    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    Result<Boolean> create(@Param("post") T post);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> delete(long postId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    boolean exists(long postId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<T> getById(long postId);

    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    Result<Boolean> vote(UserVote userVote);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Boolean> deleteVote(long voteId);

    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    Result<Boolean> changeVote(UserVote userVote);
}
