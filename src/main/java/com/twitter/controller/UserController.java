package com.twitter.controller;

import com.twitter.model.Password;
import com.twitter.model.Result;
import com.twitter.model.Role;
import com.twitter.model.User;
import com.twitter.route.Route;
import com.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by mariusz on 18.07.16.
 */
@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = Route.REGISTER_USER, method = RequestMethod.POST)
    public Result<Boolean> createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @RequestMapping(value = Route.VERIFY_USER_URL, method = RequestMethod.GET)
    public Result<Boolean> verifyUser(@PathVariable String verifyKey) {
        return userService.activateAccount(verifyKey);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.GET)
    public Result<User> getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.PUT)
    public Result<Boolean> updateUserById(@PathVariable long userId, @RequestBody Password password) {
        return userService.changeUserPasswordById(userId, password.getPassword());
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.PUT, params = "role")
    public Result<Boolean> changeUserRole(@PathVariable long userId, @RequestBody Role role) {
        return userService.changeUserRole(userId, role);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.DELETE)
    public Result<Boolean> deleteUser(@PathVariable long userId) {
        return userService.deleteUserById(userId);
    }

    @RequestMapping(value = Route.USER_GET_ALL, method = RequestMethod.GET)
    public Result<List<User>> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return userService.getAllUsers(new PageRequest(page, size));
    }

    @RequestMapping(value = Route.USER_COUNT_GET_ALL, method = RequestMethod.GET)
    public Result<Long> getAllUsersCount() {
        return userService.getAllUsersCount();
    }

    @RequestMapping(value = Route.USER_GET_FOLLOWERS, method = RequestMethod.GET)
    public Result<List<User>> getFollowersUserById(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return userService.getUserFollowersById(userId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.USER_COUNT_GET_FOLLOWERS, method = RequestMethod.GET)
    public Result<Long> getFollowersUserCount(@PathVariable long userId) {
        return userService.getUserFollowersCountById(userId);
    }

    @RequestMapping(value = Route.USER_GET_FOLLOWING, method = RequestMethod.GET)
    public Result<List<User>> getFollowingUsersById(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return userService.getUserFollowingsById(userId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.USER_COUNT_GET_FOLLOWING, method = RequestMethod.GET)
    public Result<Long> getFollowingsUserCount(@PathVariable long userId) {
        return userService.getUserFollowingCountById(userId);
    }

    @RequestMapping(value = Route.USER_FOLLOW, method = RequestMethod.POST)
    public Result<Boolean> followUser(@PathVariable long userId) {
        return userService.follow(userId);
    }

    @RequestMapping(value = Route.USER_FOLLOW, method = RequestMethod.DELETE)
    public Result<Boolean> unfollowUser(@PathVariable long userId) {
        return userService.unfollow(userId);
    }
}
