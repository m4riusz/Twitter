package com.twitter.service;

import com.twitter.model.AbstractPost;
import com.twitter.model.Result;
import com.twitter.model.UserVote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by mariusz on 05.08.16.
 */
interface AbstractPostService<T extends AbstractPost> {

    Result<Boolean> create(@Param("post") T post);

    Result<Boolean> delete(long postId);

    boolean exists(long postId);

    Result<List<T>> getAllFromUserById(long userId, Pageable pageable);

    Result<T> getById(long postId);

    Result<Boolean> vote(UserVote userVote);

    Result<Boolean> deleteVote(UserVote userVote);

    Result<Boolean> changeVote(UserVote userVote);
}
