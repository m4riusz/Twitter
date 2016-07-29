package com.twitter.service;

import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
public interface TweetService {

    Result<Tweet> getTweetById(long tweetId);

    Result<Boolean> createTweet(User user, Tweet tweet);

    Result<Boolean> deleteTweetById(long tweetId);

    Result<List<Tweet>> getAllTweets(Pageable pageable);

    Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable);

    Result<List<Tweet>> getTweetsFromUser(long userId, Pageable pageable);

    Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable);

    Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable);
}
