package com.twitter.service;

import com.twitter.model.Result;
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
public interface TweetService extends AbstractPostService<Tweet> {

    Result<List<Tweet>> getAllTweets(Pageable pageable);

    Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable);

    Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable);

    Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable);
}
