package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.exception.UserAlreadyExistsException;
import com.twitter.exception.UserFollowException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.exception.UserUnfollowException;
import com.twitter.model.Password;
import com.twitter.model.Result;
import com.twitter.model.Role;
import com.twitter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
@Transactional
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
    public Result<User> getUserById(long userId) {
        User userByID = userDao.findOne(userId);
        if (userByID == null) {
            throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        return new Result<>(true, userByID);
    }

    @Override
    public Result<Boolean> create(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_ERROR_MSG);
        }
        userDao.save(user);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> follow(User user, long userToFollowId) {
        User userToFollow = getUser(userToFollowId);
        if (user.getId() == userToFollowId) {
            throw new UserFollowException(MessageUtil.FOLLOW_YOURSELF_ERROR_MSG);
        } else if (userToFollow.getFollowers().contains(user)) {
            throw new UserFollowException(MessageUtil.FOLLOW_ALREADY_FOLLOWED_ERROR_MSG);
        }
        userToFollow.getFollowers().add(user);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> unfollow(User user, long userToUnfollowId) {
        User userToUnfollow = getUser(userToUnfollowId);
        if (user.getId() == userToUnfollow.getId()) {
            throw new UserUnfollowException(MessageUtil.UNFOLLOW_YOURSELF_ERROR_MSG);
        } else if (!userToUnfollow.getFollowers().contains(user)) {
            throw new UserUnfollowException(MessageUtil.UNFOLLOW_UNFOLLOWED_ERROR_MSG);
        }
        userToUnfollow.getFollowers().remove(user);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> banUser(long userToBanId) {
        User userToBan = getUser(userToBanId);
        userToBan.setBanned(true);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> unbanUser(long userToUnbanId) {
        User userToUnban = getUser(userToUnbanId);
        userToUnban.setBanned(false);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> changeUserRole(long userToChangeId, Role role) {
        User userToChange = getUser(userToChangeId);
        userToChange.setRole(role);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> deleteUserById(long userId) {
        if (userDao.exists(userId)) {
            userDao.delete(userId);
            return new Result<>(true, Boolean.TRUE);
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> changeUserPasswordById(long userId, String password) {
        User userToChange = getUser(userId);
        userToChange.setPassword(new Password(password));
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Long> getAllUsersCount() {
        return new Result<>(true, userDao.count());
    }

    @Override
    public Result<List<User>> getAllUsers(Pageable pageable) {
        return new Result<>(true, userDao.findAll(pageable).getContent());
    }

    @Override
    public Result<Long> getUserFollowersCountById(long userId) {
        if (userDao.exists(userId)) {
            return new Result<>(true, userDao.findFollowersCountByUserId(userId));
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<User>> getUserFollowersById(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return new Result<>(true, userDao.findFollowersByUserId(userId, pageable));
        }
        throw new UserNotFoundException();
    }

    @Override
    public Result<Long> getUserFollowingCountById(long userId) {
        if (userDao.exists(userId)) {
            return new Result<>(true, userDao.findFollowingCountByUserId(userId));
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<User>> getUserFollowingsById(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return new Result<>(true, userDao.findFollowingByUserId(userId, pageable));
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    private User getUser(long userId) {
        User userToChange = userDao.findOne(userId);
        if (userToChange == null) {
            throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        return userToChange;
    }
}
