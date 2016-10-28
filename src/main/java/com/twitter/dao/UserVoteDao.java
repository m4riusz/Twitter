package com.twitter.dao;

import com.twitter.model.AbstractPost;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mariusz on 01.08.16.
 */
@Repository
public interface UserVoteDao extends JpaRepository<UserVote, Long> {
    public UserVote findByUserAndAbstractPost(User user, AbstractPost abstractPost);

    // TODO: 28.10.16 add tests
    public long countByAbstractPostIdAndVote(long abstractPostId, Vote vote);
}
