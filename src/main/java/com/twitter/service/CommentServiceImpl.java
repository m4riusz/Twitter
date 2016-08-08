package com.twitter.service;

import com.twitter.dao.CommentDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.model.Comment;
import com.twitter.model.Result;
import com.twitter.util.MessageUtil;
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
public class CommentServiceImpl extends PostServiceImpl<Comment, CommentDao> implements CommentService {

    private TweetService tweetService;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, TweetService tweetService, UserVoteDao userVoteDao, UserService userService) {
        super(commentDao, userService, userVoteDao);
        this.tweetService = tweetService;
    }

    @Override
    public Result<List<Comment>> getTweetCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(repository.findByTweetId(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getLatestCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(repository.findByTweetIdOrderByCreateDateAsc(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getOldestCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(repository.findByTweetIdOrderByCreateDateDesc(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getMostVotedComments(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(repository.findByTweetIdOrderByVotes(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getAllFromUserById(long userId, Pageable pageable) {
        return null;
    }

    private boolean doesTweetExist(long tweetId) {
        return tweetService.exists(tweetId);
    }
}
