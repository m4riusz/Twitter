package com.twitter.service;

import com.twitter.model.Comment;
import com.twitter.model.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by mariusz on 02.08.16.
 */

@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface CommentService {

    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    Result<Boolean> createComment(@Param("post") Comment comment);

    Result<Comment> getCommentById(long commentId);

    Result<List<Comment>> getTweetCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getLatestCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getOldestCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getMostVotedComments(long tweetId, Pageable pageable);
}
