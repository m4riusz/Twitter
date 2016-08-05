package com.twitter.service;

import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface TweetService extends AbstractPostInterface {

    Result<Tweet> getTweetById(long tweetId);

    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    Result<Boolean> createTweet(@Param("post") Tweet tweet);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> deleteTweetById(long tweetId);

    Result<List<Tweet>> getAllTweets(Pageable pageable);

    Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable);

    Result<List<Tweet>> getTweetsFromUser(long userId, Pageable pageable);

    Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable);

    Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable);
}
