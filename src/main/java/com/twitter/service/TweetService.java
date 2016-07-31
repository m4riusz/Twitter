package com.twitter.service;

import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
public interface TweetService {

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Tweet> getTweetById(long tweetId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Boolean> createTweet(User user, Tweet tweet);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> deleteTweetById(long tweetId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<Tweet>> getAllTweets(Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<Tweet>> getTweetsFromUser(long userId, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable);
}
