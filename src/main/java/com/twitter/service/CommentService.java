package com.twitter.service;

import com.twitter.model.Comment;
import com.twitter.model.Result;
import com.twitter.model.UserVote;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by mariusz on 02.08.16.
 */

@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface CommentService extends AbstractPostService<Comment> {

    @Override
    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    Result<Boolean> create(@Param("post") Comment post);

    @Override
    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> delete(long postId);

    @Override
    boolean exists(long postId); // TODO: 07.08.16 add tests

    @Override
    Result<List<Comment>> getAllFromUserById(long userId, Pageable pageable);

    @Override
    Result<Comment> getById(long postId);

    @Override
    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    Result<Boolean> vote(UserVote userVote);

    @Override
    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    Result<Boolean> deleteVote(UserVote userVote);

    @Override
    @PreAuthorize(SecurityUtil.PERSONAL_VOTE)
    Result<Boolean> changeVote(UserVote userVote);

    Result<List<Comment>> getTweetCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getLatestCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getOldestCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getMostVotedComments(long tweetId, Pageable pageable);
}
