package com.twitter.dao;

import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.GET_USER_FOLLOWERS_BY_ID;
import static com.twitter.dao.Query.GET_USER_FOLLOWING_BY_ID;

/**
 * Created by mariusz on 12.07.16.
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    @Query(GET_USER_FOLLOWERS_BY_ID)
    public List<User> findFollowersByUserId(long userId);

    @Query(GET_USER_FOLLOWERS_BY_ID)
    public List<User> findFollowersByUserId(long userId, Pageable pageable);

    @Query(GET_USER_FOLLOWING_BY_ID)
    public List<User> findFollowingByUserId(long userId);

    @Query(GET_USER_FOLLOWING_BY_ID)
    public List<User> findFollowingByUserId(long userId, Pageable pageable);
}
