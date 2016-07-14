package com.twitter.service;

import com.twitter.model.Role;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
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

    public void banUser(User user, long userToBanId);

    public void unbanUser(User user, long userToUnbanId);

    public void changeUserRole(User user, long userToChangeId, Role role);

    public void deleteUserById(long userId);

    public void changeUserPasswordById(long userId, String password);

    public long getAllUsersCount();

    public List<User> getAllUsers(Pageable pageable);

    public long getUserFollowersCount();

    public List<User> getUserFollowersById(long userId, Pageable pageable);

    public long getUserFollowingCount();

    public List<User> getUserFollowingsById(long userId, Pageable pageable);

}
