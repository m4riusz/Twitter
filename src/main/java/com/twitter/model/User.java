package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Avatar avatar;
    @NotNull
    @Column(unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Length(
            min = 3, max = 10,
            message = "Username length should be between {min} and {max}!"
    )
    private String username;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Password password;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Gender gender;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean banned;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Report> reports = new ArrayList<>();
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Tweet> tweets = new ArrayList<>();
    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Tag> favouriteTags = new ArrayList<>();
    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "followerId"))
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<User> followers = new ArrayList<>();
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Tweet> favouriteTweets = new ArrayList<>();

    public User() {
        super();
        this.banned = false;
        this.role = Role.USER;
        this.avatar = new Avatar("undef", new byte[100]); // FIXME: 14.07.16 fix
    }

    public User(String username, String password, Gender gender) {
        this();
        this.username = username;
        this.password = new Password(password);
        this.gender = gender;
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

    public void setPassword(Password password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public List<Tag> getFavouriteTags() {
        return favouriteTags;
    }

    public void setFavouriteTags(List<Tag> favouriteTags) {
        this.favouriteTags = favouriteTags;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<Tweet> getFavouriteTweets() {
        return favouriteTweets;
    }

    public void setFavouriteTweets(List<Tweet> favouriteTweets) {
        this.favouriteTweets = favouriteTweets;
    }

    @Override
    public Collection<Role> getAuthorities() {
        return Arrays.asList(role);
    }

    @Override
    public String getPassword() {
        return password.getPassword();
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
        return password.getPasswordExpireDate().after(Calendar.getInstance().getTime());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
