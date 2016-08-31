package com.twitter.controller;

import com.twitter.model.*;
import com.twitter.route.Route;
import com.twitter.service.TagService;
import com.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Created by mariusz on 18.07.16.
 */
@RestController
public class UserController {

    private UserService userService;
    private TagService tagService;

    @Autowired
    public UserController(UserService userService, TagService tagService) {
        this.userService = userService;
        this.tagService = tagService;
    }

    @RequestMapping(value = Route.REGISTER_USER, method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws IOException {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @RequestMapping(value = Route.USER_AVATAR, method = RequestMethod.GET)
    public ResponseEntity<Avatar> getUserAvatar(@PathVariable long userId) {
        return new ResponseEntity<>(userService.getUserAvatar(userId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_AVATAR, method = RequestMethod.PUT)
    public ResponseEntity<Avatar> changeUserAvatar(@PathVariable long userId, @RequestBody @Valid Avatar avatar) throws IOException {
        return new ResponseEntity<>(userService.changeUserAvatar(userId, avatar), HttpStatus.OK);
    }

    @RequestMapping(value = Route.VERIFY_USER_URL, method = RequestMethod.GET)
    public ResponseEntity verifyUser(@PathVariable String verifyKey) {
        userService.activateAccount(verifyKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_URL, method = RequestMethod.GET)
    public ResponseEntity<User> getCurrentLoggedUser() {
        return new ResponseEntity<>(userService.getCurrentLoggedUser(), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.PUT)
    public ResponseEntity<User> updateUserById(@PathVariable long userId, @RequestBody Password password) {
        return new ResponseEntity<>(userService.changeUserPasswordById(userId, password.getPassword()), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.PUT, params = "role")
    public ResponseEntity<User> changeUserRole(@PathVariable long userId, @RequestParam Role role) {
        return new ResponseEntity<>(userService.changeUserRole(userId, role), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_BY_ID, method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_GET_ALL, method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(userService.getAllUsers(new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_COUNT_GET_ALL, method = RequestMethod.GET)
    public ResponseEntity<Long> getAllUsersCount() {
        return new ResponseEntity<>(userService.getAllUsersCount(), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_GET_FOLLOWERS, method = RequestMethod.GET)
    public ResponseEntity<List<User>> getFollowersUserById(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(userService.getUserFollowersById(userId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_COUNT_GET_FOLLOWERS, method = RequestMethod.GET)
    public ResponseEntity<Long> getFollowersUserCount(@PathVariable long userId) {
        return new ResponseEntity<>(userService.getUserFollowersCountById(userId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_GET_FOLLOWING, method = RequestMethod.GET)
    public ResponseEntity<List<User>> getFollowingUsersById(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(userService.getUserFollowingsById(userId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_COUNT_GET_FOLLOWING, method = RequestMethod.GET)
    public ResponseEntity<Long> getFollowingsUserCount(@PathVariable long userId) {
        return new ResponseEntity<>(userService.getUserFollowingCountById(userId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_FOLLOW, method = RequestMethod.POST)
    public ResponseEntity followUser(@PathVariable long userId) {
        userService.follow(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_FOLLOW, method = RequestMethod.DELETE)
    public ResponseEntity unfollowUser(@PathVariable long userId) {
        userService.unfollow(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_FAVOURITE_TAGS, method = RequestMethod.GET)
    public ResponseEntity<List<Tag>> getUserFavouriteTagsByUserId(@PathVariable long userId) {
        return new ResponseEntity<>(tagService.getUserFavouriteTags(userId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_FAVOURITE_TAGS, method = RequestMethod.POST)
    public ResponseEntity<Tag> addTagToUserFavouritesTags(@PathVariable long userId, @RequestBody @Valid Tag tag) {
        return new ResponseEntity<>(tagService.addFavouriteTag(userId, tag), HttpStatus.OK);
    }

    @RequestMapping(value = Route.USER_FAVOURITE_TAGS, method = RequestMethod.DELETE)
    public ResponseEntity removeTagFromUserFavouritesTags(@PathVariable long userId, @RequestBody @Valid Tag tag) {
        tagService.removeTagFromFavouriteTags(userId, tag);
        return new ResponseEntity(HttpStatus.OK);
    }
}
