package com.twitter.dao;

import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.twitter.dao.Query.*;

/**
 * Created by mariusz on 12.07.16.
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    @Query(GET_USER_FOLLOWERS_BY_ID)
    List<User> findFollowersByUserId(long userId, Pageable pageable);

    @Query(GET_USER_FOLLOWING_BY_ID)
    List<User> findFollowingByUserId(long userId, Pageable pageable);

    @Query(GET_USER_FOLLOWERS_COUNT_BY_ID)
    long findFollowersCountByUserId(long userId);

    @Query(GET_USER_FOLLOWING_COUNT_BY_ID)
    long findFollowingCountByUserId(long userId);

    User findByUsername(String username);

    User findByEmail(String email);

    User findOneByAccountStatusVerifyKey(String verifyKey);

    List<User> findByUsernameStartingWithIgnoreCase(String username, Pageable pageable);
}
