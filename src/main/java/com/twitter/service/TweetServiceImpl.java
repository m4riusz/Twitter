package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.dao.UserDao;
import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.util.TagExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.twitter.model.Result.ResultFailure;
import static com.twitter.model.Result.ResultSuccess;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
@Transactional
public class TweetServiceImpl implements TweetService {

    private final TweetDao tweetDao;
    private final UserDao userDao;
    private final TagExtractor tagExtractor;

    @Autowired
    public TweetServiceImpl(TweetDao tweetDao, UserDao userDao, TagExtractor tagExtractor) {
        this.tweetDao = tweetDao;
        this.userDao = userDao;
        this.tagExtractor = tagExtractor;
    }

    @Override
    public Result<Tweet> getTweetById(long tweetId) {
        if (tweetDao.exists(tweetId)) {
            return ResultSuccess(tweetDao.findOne(tweetId));
        }
        return ResultFailure(MessageUtil.TWEET_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> createTweet(User user, Tweet tweet) {
        if (user == null || tweet == null) {
            return ResultFailure(MessageUtil.USER_OR_TWEET_IS_NULL_MSG);
        }
        tweet.setOwner(user);
        List<Tag> tagList = tagExtractor.extract(tweet.getContent());
        tweet.setTags(tagList);
        try {
            tweetDao.save(tweet);
            return ResultSuccess(true);
        } catch (Exception e) {
            return ResultFailure(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteTweetById(long tweetId) {
        if (tweetDao.exists(tweetId)) {
            tweetDao.delete(tweetId);
            return ResultSuccess(true);
        }
        return ResultFailure(MessageUtil.TWEET_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getAllTweets(Pageable pageable) {
        return ResultSuccess(tweetDao.findAll(pageable).getContent());
    }

    @Override
    public Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return ResultSuccess(tweetDao.findTweetsFromFollowingUsers(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getTweetsFromUser(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return ResultSuccess(tweetDao.findByOwnerId(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable) {
        if (hours <= 0){
            return ResultFailure(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG);
        }
        return ResultSuccess(tweetDao.findMostPopularByVotes(hours, pageable));
    }

    @Override
    public Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable) {
        return ResultSuccess(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(tagList, pageable));
    }
}
