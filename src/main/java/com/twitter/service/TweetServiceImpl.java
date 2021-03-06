package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.exception.PostException;
import com.twitter.exception.TwitterGetException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.util.MessageUtil;
import com.twitter.util.SecurityUtil;
import com.twitter.util.extractor.UsernameExtractor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
@Transactional
public class TweetServiceImpl extends PostServiceImpl<Tweet, TweetDao> implements TweetService {

    private final TagService tagService;

    @Autowired
    public TweetServiceImpl(TweetDao tweetDao, UserService userService, UserVoteService userVoteService, TagService tagService, UsernameExtractor usernameExtractor, NotificationService notificationService) {
        super(tweetDao, userService, userVoteService, usernameExtractor, notificationService);
        this.tagService = tagService;
    }

    @Override
    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    public Tweet create(@Param("post") Tweet post) {
        List<Tag> rawTags = tagService.getTagsFromText(post.getContent());
        post.setTags(rawTags);
        return super.create(post);
    }

    @Override
    public List<Tweet> getAllTweets(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public List<Tweet> getTweetsFromFollowingUsers(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return repository.findTweetsFromFollowingUsers(userId, pageable);
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Tweet> getAllFromUserById(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return repository.findByOwnerId(userId, pageable);
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Tweet> getMostVotedTweets(int hours, Pageable pageable) {
        if (hours <= 0){
            throw new TwitterGetException(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG);
        }
        Date date = DateTime.now().minusHours(hours).toDate();
        return repository.findMostPopularAfterDateOrderByVotes(date, pageable);
    }

    @Override
    public List<Tweet> getTweetsByTagsOrderedByNewest(List<String> tags, Pageable pageable) {
        return repository.findDistinctByTagsTextInOrderByCreateDateDesc(tags, pageable);
    }

    @Override
    public List<Tweet> getTweetsByTagsOrderByPopularity(List<String> tagList, int hours, Pageable pageable) {
        if (hours <= 0){
            throw new TwitterGetException(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG);
        }
        Date date = DateTime.now().minusHours(hours).toDate();
        return repository.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(tagList,date,pageable);
    }

    @Override
    public List<Tweet> getFavouriteTweetsFromUser(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return repository.findFavouriteTweetsFromUser(userId, pageable);
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Tweet addTweetToFavourites(long tweetId) {
        User currentLoggedUser = userService.getCurrentLoggedUser();
        Tweet tweet = getById(tweetId);
        if (!isTweetInFavouritesTweets(currentLoggedUser, tweet)) {
            userService.getUserById(currentLoggedUser.getId()).getFavouriteTweets().add(tweet);
            return tweet;
        }
        throw new PostException(MessageUtil.POST_ALREADY_IN_FAVOURITES_ERROR_MSG);
    }

    @Override
    public void deleteTweetFromFavourites(long tweetId) {
        User currentUser = userService.getCurrentLoggedUser();
        Tweet tweet = getById(tweetId);
        if (isTweetInFavouritesTweets(currentUser, tweet)) {
            userService.getUserById(currentUser.getId()).getFavouriteTweets().remove(tweet);
            return;
        }
        throw new PostException(MessageUtil.POST_DOES_NOT_BELONG_TO_FAVOURITES_TWEETS_ERROR_MSG);
    }

    @Override
    public boolean tweetBelongsToFavouriteTweets(long tweetId) {
        User user = userService.getCurrentLoggedUser();
        Tweet tweet = getById(tweetId);
        return isTweetInFavouritesTweets(user, tweet);
    }

    private boolean isTweetInFavouritesTweets(User currentLoggedUser, Tweet tweet) {
        return repository.doesTweetBelongToUserFavouritesTweets(currentLoggedUser.getId(), tweet.getId());
    }
}
