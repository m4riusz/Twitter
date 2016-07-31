package com.twitter.controller;

import com.twitter.model.Password;
import com.twitter.model.Result;
import com.twitter.model.User;
import com.twitter.route.Route;
import com.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = Route.REGISTER_USER, method = RequestMethod.POST)
    public Result<Boolean> createUser(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @RequestMapping(value = Route.VERIFY_USER_URL, method = RequestMethod.GET)
    public Result<String> verifyUser(@PathVariable String verifyKey) {
        return userService.activateAccount(verifyKey);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.GET)
    public Result<User> getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.PUT)
    public void updateUserById(@PathVariable long userId, @RequestBody Password password) {
        userService.changeUserPasswordById(userId, password.getPassword());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }

    @RequestMapping(value = Route.USER_GET_ALL, method = RequestMethod.GET)
    public Result<List<User>> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return userService.getAllUsers(new PageRequest(page, size));
    }
}
