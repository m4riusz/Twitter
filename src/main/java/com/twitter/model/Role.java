package com.twitter.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by mariusz on 11.07.16.
 */
public enum Role implements GrantedAuthority {
    USER("USER"),
    ADMIN("ADMIN"),
    MOD("MODERATOR");

    private String roleName;

    Role(String role) {
        this.roleName = role;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
