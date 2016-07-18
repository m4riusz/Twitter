package com.twitter.controller;

import com.twitter.model.Password;
import com.twitter.model.User;
import com.twitter.route.Route;
import com.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mariusz on 18.07.16.
 */
@RestController
@RequestMapping(UserController.API)
public class UserController {
    public static final String API = "/api";
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = Route.REST_USER, method = RequestMethod.POST)
    public void createUser(@RequestBody User user) {
        userService.create(user);
    }

    @RequestMapping(value = Route.REST_USER_ID, method = RequestMethod.GET)
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @RequestMapping(value = Route.REST_USER_ID, method = RequestMethod.PUT)
    public void updateUserById(@PathVariable long userId, @RequestBody Password password) {
        userService.changeUserPasswordById(userId, password.getPassword());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = Route.REST_USER_ID, method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }

    @RequestMapping(value = Route.REST_USER_GET_ALL, method = RequestMethod.GET)
    public List<User> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return userService.getAllUsers(new PageRequest(page, size));
    }
}
