package com.twitter.dao;

import com.twitter.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.SELECT_COMMENT_FROM_TWEET_ORDER_BY_VOTE_SIZE;

/**
 * Created by mariusz on 21.07.16.
 */
@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {

    List<Comment> findByTweetId(long tweetId, Pageable pageable);

    List<Comment> findByTweetIdOrderByCreateDateAsc(long tweetId, Pageable pageable);

    List<Comment> findByTweetIdOrderByCreateDateDesc(long tweetId, Pageable pageable);

    List<Comment> findByOwnerId(long userId, Pageable pageable);

    @Query(value = SELECT_COMMENT_FROM_TWEET_ORDER_BY_VOTE_SIZE)
    List<Comment> findByTweetIdOrderByVotesAscCreateDateDesc(long tweetId, Pageable pageable);
}
