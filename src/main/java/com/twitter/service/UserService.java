package com.twitter.service;

import com.twitter.model.Avatar;
import com.twitter.model.Role;
import com.twitter.model.User;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
public interface UserService extends UserDetailsService {

    User create(User user) throws IOException;

    User loadUserByUsername(String username) throws UsernameNotFoundException;

    void activateAccount(String verifyKey);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    User getCurrentLoggedUser();

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    boolean exists(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    User getUserById(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    void follow(long userToFollowId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    void unfollow(long userToUnfollowId);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    void banUser(long userToBanId, Date date);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    void unbanUser(long userToUnbanId);

    @PreAuthorize(SecurityUtil.ADMIN)
    User changeUserRole(long userToChangeId, Role role);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    void deleteUserById(long userId);

    @PreAuthorize(SecurityUtil.PERSONAL_USAGE)
    User changeUserPasswordById(@Param("userId") long userId, String password);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Long getAllUsersCount();

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    List<User> getAllUsers(Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Long getUserFollowersCountById(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    List<User> getUserFollowersById(long userId, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Long getUserFollowingCountById(long userId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    List<User> getUserFollowingsById(long userId, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Avatar getUserAvatar(long userId); // TODO: 15.08.16 add tests

    @PreAuthorize(SecurityUtil.PERSONAL_USAGE)
    Avatar changeUserAvatar(long userId, Avatar avatar) throws IOException; // TODO: 15.08.16 add tests
}
