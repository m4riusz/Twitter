package com.twitter.service;

import com.twitter.model.Tweet;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface TweetService extends PostService<Tweet> {

    List<Tweet> getAllFromUserById(long userId, Pageable pageable);

    List<Tweet> getAllTweets(Pageable pageable);

    List<Tweet> getTweetsFromFollowingUsers(long userId, Pageable pageable);

    List<Tweet> getMostVotedTweets(int hours, Pageable pageable);

    List<Tweet> getTweetsByTagsOrderedByNewest(List<String> tagList, Pageable pageable);

    // TODO: 09.11.16 tests
    List<Tweet> getTweetsByTagsOrderByPopularity(List<String> tagList, int hours, Pageable pageable);

    List<Tweet> getFavouriteTweetsFromUser(long userId, Pageable pageable);

    Tweet addTweetToFavourites(long tweetId);

    void deleteTweetFromFavourites(long tweetId);

    boolean tweetBelongsToFavouriteTweets(long tweetId);
}
