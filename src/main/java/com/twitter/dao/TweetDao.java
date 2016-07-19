package com.twitter.dao;

import com.twitter.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mariusz on 19.07.16.
 */
@Repository
public interface TweetDao extends JpaRepository<Tweet, Long> {
}
