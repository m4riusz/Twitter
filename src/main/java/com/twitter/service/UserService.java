package com.twitter.service;

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

    public User loadUserByUsername(String username) throws UsernameNotFoundException;

    public User getUserById(long userId);

    public void create(User user);

    public void follow(User user, long usedToFollowId);

    public void unfollow(User user, long userToUnfollowId);

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public void banUser(long userToBanId);

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public void unbanUser(long userToUnbanId);

    @PreAuthorize("hasRole('ADMIN')")
    public void changeUserRole(long userToChangeId, Role role);

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public void deleteUserById(long userId);

    @PreAuthorize("#userToChangeId == principal.id")
    public void changeUserPasswordById(@Param("userToChangeId") long userId, String password);

    public long getAllUsersCount();

    public List<User> getAllUsers(Pageable pageable);

    public long getUserFollowersCountById(long userId);

    public List<User> getUserFollowersById(long userId, Pageable pageable);

    public long getUserFollowingCountById(long userId);

    public List<User> getUserFollowingsById(long userId, Pageable pageable);

}
