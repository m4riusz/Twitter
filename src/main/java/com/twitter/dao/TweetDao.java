package com.twitter.dao;

import com.twitter.model.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mariusz on 19.07.16.
 */
@Repository
public interface TweetDao extends JpaRepository<Tweet, Long> {

    public List<Tweet> findByOwnerId(long userId, Pageable pageable);

    @Query("SELECT tweet.comments FROM Tweet tweet WHERE tweet.id = ?1")
    public List<Tweet> findCommentsById(long tweetId, Pageable pageable);
}

