package com.twitter.service;

import com.twitter.dao.CommentDao;
import com.twitter.dao.TweetDao;
import com.twitter.dao.UserDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.model.Comment;
import com.twitter.model.Result;
import com.twitter.model.UserVote;
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
public class CommentServiceImpl implements CommentService {


    private CommentDao commentDao;
    private TweetDao tweetDao;
    private UserVoteDao userVoteDao;
    private UserDao userDao;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, TweetDao tweetDao, UserVoteDao userVoteDao, UserDao userDao) {
        this.commentDao = commentDao;
        this.tweetDao = tweetDao;
        this.userVoteDao = userVoteDao;
        this.userDao = userDao;
    }

    @Override
    public Result<Boolean> create(Comment comment) {
        if (commentDao.save(comment) != null) {
            return ResultSuccess(true);
        }
        return ResultFailure(MessageUtil.SAVE_COMMENT_ERROR);
    }

    @Override
    public Result<Boolean> delete(long postId) {
        if (doesCommentExist(postId)) {
            commentDao.delete(postId);
            return ResultSuccess(true);
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getAllFromUserById(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return ResultSuccess(commentDao.findByOwnerId(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Comment> getById(long commentId) {
        if (doesCommentExist(commentId)) {
            return ResultSuccess(commentDao.findOne(commentId));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    private boolean doesCommentExist(long commentId) {
        return commentDao.exists(commentId);
    }

    @Override
    public Result<Boolean> vote(UserVote userVote) {
        if (!doesUserExist(userVote.getUser().getId())) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesTweetExist(userVote.getAbstractPost().getId())) {
            return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (doesVoteExist(userVote)) {
            return ResultFailure(MessageUtil.POST_ALREADY_VOTED);
        }
        userVoteDao.save(userVote);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> deleteVote(UserVote userVote) {
        if (!doesUserExist(userVote.getUser().getId())) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesTweetExist(userVote.getAbstractPost().getId())) {
            return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesVoteExist(userVote)) {
            return ResultFailure(MessageUtil.NOT_VOTE_ERROR_MSG);
        }
        userVoteDao.delete(userVote);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> changeVote(UserVote userVote) {
        if (!doesUserExist(userVote.getUser().getId())) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesTweetExist(userVote.getAbstractPost().getId())) {
            return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesVoteExist(userVote)) {
            return ResultFailure(MessageUtil.NOT_VOTE_ERROR_MSG);
        }
        UserVote userVote2 = userVoteDao.findByUserAndAbstractPost(userVote.getUser(), userVote.getAbstractPost());
        userVote2.setVote(userVote.getVote());
        return ResultSuccess(true);
    }


    @Override
    public Result<List<Comment>> getTweetCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(commentDao.findByTweetId(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getLatestCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(commentDao.findByTweetIdOrderByCreateDateAsc(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getOldestCommentsById(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(commentDao.findByTweetIdOrderByCreateDateDesc(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Comment>> getMostVotedComments(long tweetId, Pageable pageable) {
        if (doesTweetExist(tweetId)) {
            return ResultSuccess(commentDao.findByTweetIdOrderByVotes(tweetId, pageable));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    private boolean doesTweetExist(long tweetId) {
        return tweetDao.exists(tweetId);
    }


    private boolean doesUserExist(long id) {
        return userDao.exists(id);
    }

    private boolean doesVoteExist(UserVote userVote) {
        return userVoteDao.findByUserAndAbstractPost(userVote.getUser(), userVote.getAbstractPost()) != null;
    }
}
