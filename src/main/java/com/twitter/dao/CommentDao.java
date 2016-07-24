package com.twitter.dao;

import com.twitter.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.SELECT_MOST_POPULAR_COMMENTS_FROM_TWEET;

/**
 * Created by mariusz on 21.07.16.
 */
@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {

    public List<Comment> findByTweetId(long tweetId, Pageable pageable);

    public List<Comment> findByTweetIdOrderByCreateDateAsc(long tweetId, Pageable pageable);

    public List<Comment> findByTweetIdOrderByCreateDateDesc(long tweetId, Pageable pageable);

    @Query(SELECT_MOST_POPULAR_COMMENTS_FROM_TWEET)
    public List<Comment> findByTweetIdOrderByVotes(long tweetId, Pageable pageable);

}
