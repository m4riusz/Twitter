package com.twitter.dao;

import com.twitter.model.*;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by mariusz on 12.07.16.
 */
public final class UserBuilder {
    private int id;
    private Avatar avatar;
    private String username;
    private String password;
    private DateTime passwordExpireDate;
    private boolean enable;
    private boolean banned;
    private Role role;
    private Set<Report> reports;
    private Set<Tweet> tweets;
    private Set<Tag> favouriteTags;
    private Set<User> followers;
    private Set<User> following;

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public UserBuilder withAvatar(Avatar avatar) {
        this.avatar = avatar;
        return this;
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withPasswordExpireDate(DateTime passwordExpireDate) {
        this.passwordExpireDate = passwordExpireDate;
        return this;
    }

    public UserBuilder withEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public UserBuilder withBanned(boolean banned) {
        this.banned = banned;
        return this;
    }

    public UserBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserBuilder withReports(Set<Report> reports) {
        this.reports = reports;
        return this;
    }

    public UserBuilder withTweets(Set<Tweet> tweets) {
        this.tweets = tweets;
        return this;
    }

    public UserBuilder withFavouriteTags(Set<Tag> favouriteTags) {
        this.favouriteTags = favouriteTags;
        return this;
    }

    public UserBuilder withFollowers(Set<User> followers) {
        this.followers = followers;
        return this;
    }

    public UserBuilder withFollowing(Set<User> following) {
        this.following = following;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setAvatar(avatar);
        user.setUsername(username);
        user.setPassword(password);
        user.setPasswordExpireDate(passwordExpireDate);
        user.setEnable(enable);
        user.setBanned(banned);
        user.setRole(role);
        user.setReports(reports);
        user.setTweets(tweets);
        user.setFavouriteTags(favouriteTags);
        user.setFollowers(followers);
        user.setFollowing(following);
        return user;
    }
}
