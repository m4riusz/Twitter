package com.twitter.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.twitter.util.Config.MAX_PASSWORD_LENGTH;
import static com.twitter.util.Config.MIN_PASSWORD_LENGTH;

/**
 * Created by mariusz on 15.10.16.
 */
public class PasswordChange {
    @NotNull
    @Length(
            min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH,
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
