package com.twitter.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 15.10.16.
 */
public class PasswordChange {
    @NotNull
    @Length(
            min = 6, max = 10,
            message = "Password length should be between {min} and {max}!"
    )
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
