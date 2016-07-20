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
    private long id = 0;
    private Avatar avatar = new Avatar("avatar.jpg", new byte[10]);
    private String username = "User_" + id;
    private Password password = new Password("password");
    private Role role = Role.USER;
    private Gender gender = Gender.UNDEFINED;
    private boolean banned = false;
    private Actions actions = new Actions();
    private List<User> followers = new ArrayList<>();
    private Date createDate = Calendar.getInstance().getTime();

    private UserBuilder() {
    }

    public static UserBuilder user() {
        return new UserBuilder();
    }

    public UserBuilder withAvatar(Avatar avatar) {
        this.avatar = avatar;
        return this;
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
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

    public UserBuilder withBanned(boolean banned) {
        this.banned = banned;
        return this;
    }

    public UserBuilder withActions(Actions actions) {
        this.actions = actions;
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
        user.setBanned(banned);
        user.setActions(actions);
        user.setFollowers(followers);
        user.setId(id);
        user.setCreateDate(createDate);
        return user;
    }

}
