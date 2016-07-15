package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.exception.UserFollowException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.exception.UserUnfollowException;
import com.twitter.model.Password;
import com.twitter.model.Role;
import com.twitter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public User getUserById(long userId) {
        return getUser(userId);
    }

    @Override
    public void create(User user) {
        userDao.save(user);
    }

    @Override
    public void follow(User user, long usedToFollowId) {
        User userToFollow = getUser(usedToFollowId);
        if (user.getId() == usedToFollowId) {
            throw new UserFollowException();
        } else if (userToFollow.getFollowers().contains(user)) {
            throw new UserFollowException();
        }
        userToFollow.getFollowers().add(user);
    }

    @Override
    public void unfollow(User user, long userToUnfollowId) {
        User userToUnfollow = getUser(userToUnfollowId);
        if (user.getId() == userToUnfollow.getId()) {
            throw new UserUnfollowException();
        } else if (!userToUnfollow.getFollowers().contains(user)) {
            throw new UserUnfollowException();
        }
        userToUnfollow.getFollowers().remove(user);
    }

    @Override   // TODO: 15.07.16 add security to moderator and admin
    public void banUser(User user, long userToBanId) {
        User userToBan = getUser(userToBanId);
        userToBan.setBanned(true);
    }

    @Override   // TODO: 15.07.16 add security to moderator and admin
    public void unbanUser(User user, long userToUnbanId) {
        User userToUnban = getUser(userToUnbanId);
        userToUnban.setBanned(false);
    }

    @Override   // TODO: 15.07.16 add security
    public void changeUserRole(User user, long userToChangeId, Role role) {
        User userToChange = getUser(userToChangeId);
        userToChange.setRole(role);
    }

    @Override   // TODO: 15.07.16 add security
    public void deleteUserById(long userId) {
        userDao.delete(userId);
    }

    @Override // TODO: 15.07.16 add security
    public void changeUserPasswordById(long userId, String password) {
        User userToChange = getUser(userId);
        userToChange.setPassword(new Password(password));
    }

    @Override
    public long getAllUsersCount() {
        return userDao.count();
    }

    @Override
    public List<User> getAllUsers(Pageable pageable) {
        return userDao.findAll(pageable).getContent();
    }

    @Override
    public long getUserFollowersCountById(long userId) {
        getUser(userId);
        return userDao.findFollowersCountByUserId(userId);
    }

    @Override
    public List<User> getUserFollowersById(long userId, Pageable pageable) {
        getUser(userId);
        return userDao.findFollowersByUserId(userId, pageable);
    }

    @Override
    public long getUserFollowingCountById(long userId) {
        getUser(userId);
        return userDao.findFollowingCountByUserId(userId);
    }

    @Override
    public List<User> getUserFollowingsById(long userId, Pageable pageable) {
        getUser(userId);
        return userDao.findFollowingByUserId(userId, pageable);
    }

    private User getUser(long userId) {
        User userToChange = userDao.findOne(userId);
        if (userToChange == null) {
            throw new UserNotFoundException();
        }
        return userToChange;
    }
}
