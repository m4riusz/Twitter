package com.twitter.service;

import com.twitter.model.Result;
import com.twitter.model.Role;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
public interface UserService extends UserDetailsService {

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<User> getUserById(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Boolean> create(User user);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Boolean> follow(User user, long userToFollowId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Boolean> unfollow(User user, long userToUnfollowId);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> banUser(long userToBanId);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> unbanUser(long userToUnbanId);

    @PreAuthorize(SecurityUtil.ADMIN)
    Result<Boolean> changeUserRole(long userToChangeId, Role role);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> deleteUserById(long userId);

    @PreAuthorize("#userToChangeId == principal.id") //// TODO: 27.07.16 add to security util
    Result<Boolean> changeUserPasswordById(@Param("userToChangeId") long userId, String password);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Long> getAllUsersCount();

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<User>> getAllUsers(Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Long> getUserFollowersCountById(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<User>> getUserFollowersById(long userId, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Long> getUserFollowingCountById(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<List<User>> getUserFollowingsById(long userId, Pageable pageable);

}
