package com.twitter.model;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Avatar avatar;
    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Password password;
    @NotNull
    private Role role;
    @NotNull
    private Gender gender;
    @NotNull
    private boolean banned;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Actions actions;
    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "followerId"))
    private List<User> followers;

    public User() {
        super();
        this.banned = false;
        this.role = Role.USER;
        this.actions = new Actions();
        this.followers = new ArrayList<>();
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

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
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
        return password.getPasswordExpireDate().isAfterNow();
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
