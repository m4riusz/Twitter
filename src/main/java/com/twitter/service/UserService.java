package com.twitter.service;

import com.twitter.model.Result;
import com.twitter.model.Role;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
public interface UserService extends UserDetailsService {

    User loadUserByUsername(String username) throws UsernameNotFoundException;

    Result<User> getUserById(long userId);

    Result<Boolean> create(User user);

    Result<Boolean> follow(User user, long usedToFollowId);

    Result<Boolean> unfollow(User user, long userToUnfollowId);

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    Result<Boolean> banUser(long userToBanId);

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    Result<Boolean> unbanUser(long userToUnbanId);

    @PreAuthorize("hasRole('ADMIN')")
    Result<Boolean> changeUserRole(long userToChangeId, Role role);

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    Result<Boolean> deleteUserById(long userId);

    @PreAuthorize("#userToChangeId == principal.id")
    Result<Boolean> changeUserPasswordById(@Param("userToChangeId") long userId, String password);

    Result<Long> getAllUsersCount();

    Result<List<User>> getAllUsers(Pageable pageable);

    Result<Long> getUserFollowersCountById(long userId);

    Result<List<User>> getUserFollowersById(long userId, Pageable pageable);

    Result<Long> getUserFollowingCountById(long userId);

    Result<List<User>> getUserFollowingsById(long userId, Pageable pageable);

}
