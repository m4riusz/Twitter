package com.twitter.service;

import com.twitter.model.AbstractPost;
import com.twitter.model.Result;
import com.twitter.model.UserVote;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 05.08.16.
 */
@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface AbstractPostService<T extends AbstractPost> {

    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    public Result<Boolean> create(@Param("post") T post);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    public Result<Boolean> delete(long postId);

    public Result<List<T>> getAllFromUserById(long userId, Pageable pageable);

    public Result<T> getById(long postId);

    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    public Result<Boolean> vote(UserVote userVote);

    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    public Result<Boolean> deleteVote(UserVote userVote);

    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    public Result<Boolean> changeVote(UserVote userVote);
}
