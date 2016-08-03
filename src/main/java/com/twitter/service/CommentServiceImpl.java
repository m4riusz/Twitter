package com.twitter.service;

import com.twitter.dao.CommentDao;
import com.twitter.dao.TweetDao;
import com.twitter.model.Comment;
import com.twitter.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.twitter.model.Result.ResultFailure;
import static com.twitter.model.Result.ResultSuccess;

/**
 * Created by mariusz on 03.08.16.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private CommentDao commentDao;
    private TweetDao tweetDao;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, TweetDao tweetDao) {
        this.commentDao = commentDao;
        this.tweetDao = tweetDao;
    }

    @Override
    public Result<Boolean> createComment(Comment comment) {
        try {
            commentDao.save(comment);
            return ResultSuccess(true);
        } catch (Exception e) {
            return ResultFailure(e.getMessage());
        }
    }

    @Override
    public Result<Comment> getCommentById(long commentId) {
        if (commentDao.exists(commentId)) {
            return ResultSuccess(commentDao.findOne(commentId));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getTweetCommentsById(long tweetId, Pageable pageable) {
        if (tweetDao.exists(tweetId)) {
            return ResultSuccess(commentDao.findByTweetId(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getLatestCommentsById(long tweetId, Pageable pageable) {
        if (tweetDao.exists(tweetId)) {
            return ResultSuccess(commentDao.findByTweetIdOrderByCreateDateAsc(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getOldestCommentsById(long tweetId, Pageable pageable) {
        if (tweetDao.exists(tweetId)) {
            return ResultSuccess(commentDao.findByTweetIdOrderByCreateDateDesc(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getMostVotedComments(long tweetId, Pageable pageable) {
        if (tweetDao.exists(tweetId)) {
            return ResultSuccess(commentDao.findByTweetId(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }
}
