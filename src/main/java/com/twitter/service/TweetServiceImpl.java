package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.dao.UserDao;
import com.twitter.exception.TweetCreateException;
import com.twitter.exception.TweetNotFoundException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
public class TweetServiceImpl implements TweetService {

    private final TweetDao tweetDao;
    private final UserDao userDao;

    @Autowired
    public TweetServiceImpl(TweetDao tweetDao, UserDao userDao) {
        this.tweetDao = tweetDao;
        this.userDao = userDao;
    }

    @Override
    public Result<Tweet> getTweetById(long tweetId) {
        if (tweetDao.exists(tweetId)) {
            return new Result<>(true, tweetDao.findOne(tweetId));
        }
        throw new TweetNotFoundException(MessageUtil.TWEET_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> createTweet(User user, Tweet tweet) {
        if (user == null || tweet == null) {
            throw new TweetCreateException(MessageUtil.USER_OR_TWEET_IS_NULL_MSG);
        }
        tweet.setOwner(user);
        tweetDao.save(tweet);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> deleteTweetById(long tweetId) {
        if (tweetDao.exists(tweetId)) {
            tweetDao.delete(tweetId);
            return new Result<>(true, Boolean.TRUE);
        }
        throw new TweetNotFoundException(MessageUtil.TWEET_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getAllTweets(Pageable pageable) {
        return new Result<>(true, tweetDao.findAll(pageable).getContent());
    }

    @Override
    public Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return new Result<>(true, tweetDao.findTweetsFromFollowingUsers(userId, pageable));
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getTweetsFromUser(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return new Result<>(true, tweetDao.findByOwnerId(userId, pageable));
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable) {
        return new Result<>(true, tweetDao.findMostPopularByVotes(hours, pageable));
    }

    @Override
    public Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable) {
        return new Result<>(true, tweetDao.findDistinctByTagsInOrderByCreateDateDesc(tagList, pageable));
    }
}
