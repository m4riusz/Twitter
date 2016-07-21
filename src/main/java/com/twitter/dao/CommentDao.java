package com.twitter.dao;

import com.twitter.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.GET_COMMENTS_FROM_TWEET_BY_ID;

/**
 * Created by mariusz on 21.07.16.
 */
@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {

    @Query(GET_COMMENTS_FROM_TWEET_BY_ID)
    public List<Comment> findCommentsById(long tweetId, Pageable pageable);

}
