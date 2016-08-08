package com.twitter.service;

import com.twitter.model.Comment;
import com.twitter.model.Result;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by mariusz on 02.08.16.
 */

@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface CommentService extends PostService<Comment> {

    Result<List<Comment>> getTweetCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getLatestCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getOldestCommentsById(long tweetId, Pageable pageable);

    Result<List<Comment>> getMostVotedComments(long tweetId, Pageable pageable);

    Result<List<Comment>> getAllFromUserById(long userId, Pageable pageable);

}
