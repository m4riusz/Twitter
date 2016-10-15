package com.twitter.dto;

import com.twitter.model.Role;

import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 15.10.16.
 */
public class RoleChange {
    @NotNull
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
