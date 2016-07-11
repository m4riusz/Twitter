package com.twitter.model;

import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
    @OneToOne
    @JoinColumn
    private Avatar avatar;
    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    private String password;
    @NotNull
    private DateTime passwordExpireDate;
    @NotNull
    private boolean enable;
    @NotNull
    private boolean banned;
    @NotNull
    private Role role;
    @NotNull
    @OneToMany
    private Set<Report> reports;
    @NotNull
    @OneToMany
    private Set<Tweet> tweets;
    @NotNull
    @ManyToMany
    private Set<Tag> favouriteTags;
    @NotNull
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "followerId"))
    private Set<User> followers;
    @NotNull
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "followingUserId"))
    private Set<User> following;

    public User() {
        this.passwordExpireDate = DateTime.now();
        this.enable = true;
        this.banned = false;
        this.role = Role.USER;
        this.reports = new HashSet<>();
        this.tweets = new HashSet<>();
        this.favouriteTags = new HashSet<>();
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    public User(Avatar avatar, String username, String password) {
        super();
        this.avatar = avatar;
        this.username = username;
        this.password = password;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DateTime getPasswordExpireDate() {
        return passwordExpireDate;
    }

    public void setPasswordExpireDate(DateTime passwordExpireDate) {
        this.passwordExpireDate = passwordExpireDate;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    public Set<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Set<Tweet> tweets) {
        this.tweets = tweets;
    }

    public Set<Tag> getFavouriteTags() {
        return favouriteTags;
    }

    public void setFavouriteTags(Set<Tag> favouriteTags) {
        this.favouriteTags = favouriteTags;
    }

    @Override
    public Collection<Role> getAuthorities() {
        return Arrays.asList(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return banned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return passwordExpireDate.isAfterNow();
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
