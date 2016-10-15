package com.twitter.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 15.10.16.
 */
public class PasswordChange {
    @NotNull

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
