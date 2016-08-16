package com.twitter.service;

import com.twitter.dao.CommentDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.exception.PostNotFoundException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.Comment;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mariusz on 03.08.16.
 */
@Service
@Transactional
public class CommentServiceImpl extends PostServiceImpl<Comment, CommentDao> implements CommentService {

    private TweetService tweetService;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, TweetService tweetService, UserVoteService userVoteService, UserService userService) {
        super(commentDao, userService, userVoteService);
        this.tweetService = tweetService;
    }

    @Override
    public List<Comment> getTweetCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return repository.findByTweetId(tweetId, pageable);
        }
        throw new PostNotFoundException(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Comment> getLatestCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return repository.findByTweetIdOrderByCreateDateAsc(tweetId, pageable);
        }
        throw new PostNotFoundException(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Comment> getOldestCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return repository.findByTweetIdOrderByCreateDateDesc(tweetId, pageable);
        }
        throw new PostNotFoundException(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Comment> getMostVotedComments(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return repository.findByTweetIdOrderByVotes(tweetId, pageable);
        }
        throw new PostNotFoundException(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Comment> getAllFromUserById(long userId, Pageable pageable) {
        if (!doesUserExist(userId)) {
            throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        return repository.findByOwnerId(userId, pageable);
    }

    private boolean doesTweetExist(long tweetId) {
        return tweetService.exists(tweetId);
    }
}
