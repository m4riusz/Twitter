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

    @Override
    public void banUser(long userToBanId) {
        User userToBan = getUser(userToBanId);
        userToBan.setBanned(true);
    }

    @Override
    public void unbanUser(long userToUnbanId) {
        User userToUnban = getUser(userToUnbanId);
        userToUnban.setBanned(false);
    }

    @Override
    public void changeUserRole(long userToChangeId, Role role) {
        User userToChange = getUser(userToChangeId);
        userToChange.setRole(role);
    }

    @Override
    public void deleteUserById(long userId) {
        if (userDao.exists(userId)) {
            userDao.delete(userId);
            return;
        }
        throw new UserNotFoundException();
    }

    @Override
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
        if (userDao.exists(userId)) {
            return userDao.findFollowersCountByUserId(userId);
        }
        throw new UserNotFoundException();
    }

    @Override
    public List<User> getUserFollowersById(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return userDao.findFollowersByUserId(userId, pageable);
        }
        throw new UserNotFoundException();
    }

    @Override
    public long getUserFollowingCountById(long userId) {
        if (userDao.exists(userId)) {
            return userDao.findFollowingCountByUserId(userId);
        }
        throw new UserNotFoundException();
    }

    @Override
    public List<User> getUserFollowingsById(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return userDao.findFollowingByUserId(userId, pageable);
        }
        throw new UserNotFoundException();
    }

    private User getUser(long userId) {
        User userToChange = userDao.findOne(userId);
        if (userToChange == null) {
            throw new UserNotFoundException();
        }
        return userToChange;
    }
}
