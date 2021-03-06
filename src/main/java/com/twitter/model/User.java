package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;

import static com.twitter.config.DatabaseConfig.USERNAME_PATTERN;
import static com.twitter.util.Config.MAX_USERNAME_LENGTH;
import static com.twitter.util.Config.MIN_USERNAME_LENGTH;

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
    @Pattern(regexp = USERNAME_PATTERN, message = "Username should contains only letters, numbers and '_' sign!")
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Length(
            min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH,
            message = "Username length should be between {min} and {max}!"
    )
    private String username;
    @Email(message = "Wrong email format!")
    @NotNull(message = "Email is required!")
    @Column(unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String email;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Password password;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Gender gender;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private AccountStatus accountStatus;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @JsonIgnore
    private List<Report> reports = new ArrayList<>();
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Tweet> tweets = new ArrayList<>();
    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Tag> favouriteTags = new ArrayList<>();
    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "followerId"))
    @JsonIgnore
    private List<User> followers = new ArrayList<>();
    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Tweet> favouriteTweets = new ArrayList<>();

    public User() {
        super();
        this.accountStatus = new AccountStatus();
        this.role = Role.USER;
        this.gender = Gender.UNDEFINED;
        this.avatar = new Avatar();
    }

    public User(String email, String username, String password, Gender gender) {
        this();
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    @JsonIgnore
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

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return !accountStatus.isDeleted();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return (accountStatus.getBannedUntil() == null || accountStatus.getBannedUntil().before(Calendar.getInstance().getTime()));
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return password.getPasswordExpireDate().after(Calendar.getInstance().getTime());
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return accountStatus.isEnable();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        return username != null ? username.equals(user.username) : user.username == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
