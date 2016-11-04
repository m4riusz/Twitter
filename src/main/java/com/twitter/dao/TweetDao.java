package com.twitter.dao;

import com.twitter.model.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.*;

/**
 * Created by mariusz on 19.07.16.
 */
@Repository
public interface TweetDao extends JpaRepository<Tweet, Long> {

    List<Tweet> findByOwnerId(long userId, Pageable pageable);

    @Query(value = SELECT_MOST_POPULAR_TWEETS_BY_TIME)
    List<Tweet> findMostPopularByVotes(int hours, Pageable pageable);

    List<Tweet> findDistinctByTagsTextInOrderByCreateDateDesc(List<String> tagList, Pageable pageable);

    @Query(value = SELECT_NEWEST_TWEETS_FROM_FOLLOWERS)
    List<Tweet> findTweetsFromFollowingUsers(long userId, Pageable pageable);

    @Query(value = SELECT_FAVOURITE_TWEETS_FROM_USER)
    List<Tweet> findFavouriteTweetsFromUser(long userId, Pageable pageable);

    @Query(value = TWEET_EXISTS_IN_USER_FAVOURITES_TWEETS)
    boolean doesTweetBelongToUserFavouritesTweets(long userId, long postId);
}

