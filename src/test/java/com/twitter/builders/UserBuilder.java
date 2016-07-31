package com.twitter.builders;


import com.twitter.Builder;
import com.twitter.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mariusz on 15.07.16.
 */
public final class UserBuilder implements Builder<User> {
    private static long counter = 0L;
    private long id;
    private Avatar avatar = new Avatar("avatar.jpg", new byte[10]);
    private String username = "User_" + counter;
    private String email = username + "@email.com";
    private Password password = new Password("password");
    private Role role = Role.USER;
    private Gender gender = Gender.UNDEFINED;
    private AccountStatus accountStatus = new AccountStatus(true, username + "_verifyKey");
    private List<Report> reports = new ArrayList<>();
    private List<Tweet> tweets = new ArrayList<>();
    private List<Tag> favouriteTags = new ArrayList<>();
    private List<User> followers = new ArrayList<>();
    private Date createDate = Calendar.getInstance().getTime();

    public static UserBuilder user() {
        counter++;
        return new UserBuilder();
    }

    public UserBuilder withReports(List<Report> reports) {
        this.reports = reports;
        return this;
    }

    public UserBuilder withTweets(List<Tweet> tweets) {
        this.tweets = tweets;
        return this;
    }

    public UserBuilder withFavouriteTags(List<Tag> tags) {
        this.favouriteTags = tags;
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

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(Password password) {
        this.password = password;
        return this;
    }

    public UserBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserBuilder withAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public UserBuilder withFollowers(List<User> followers) {
        this.followers = followers;
        return this;
    }

    public UserBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public User build() {
        User user = new User();
        user.setAvatar(avatar);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setGender(gender);
        user.setEmail(email);
        user.setAccountStatus(accountStatus);
        user.setTweets(tweets);
        user.setFavouriteTags(favouriteTags);
        user.setReports(reports);
        user.setFollowers(followers);
        user.setId(id);
        user.setCreateDate(createDate);
        return user;
    }

}
