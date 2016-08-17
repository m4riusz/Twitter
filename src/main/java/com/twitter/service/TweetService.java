package com.twitter.service;

import com.twitter.model.Tag;
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

    List<Tweet> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable);

    List<Tweet> getFavouriteTweetsFromUser(long userId, Pageable pageable);
}
