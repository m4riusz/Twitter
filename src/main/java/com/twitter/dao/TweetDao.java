package com.twitter.dao;

import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.SELECT_MOST_POPULAR_TWEETS_BY_TIME;

/**
 * Created by mariusz on 19.07.16.
 */
@Repository
public interface TweetDao extends JpaRepository<Tweet, Long> {

    public List<Tweet> findByOwnerId(long userId, Pageable pageable);

    @Query(value = SELECT_MOST_POPULAR_TWEETS_BY_TIME)
    public List<Tweet> findMostPopularByVotes(int hours, Pageable pageable);

    public List<Tweet> findDistinctByTagsInOrderByCreateDateDesc(List<Tag> tagList, Pageable pageable);

}

