package com.twitter.dto;

import com.twitter.model.Gender;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.twitter.config.DatabaseConfig.USERNAME_PATTERN;
import static com.twitter.util.Config.*;

/**
 * Created by mariusz on 22.11.16.
 */
public class UserCreateForm {

    @NotNull
    @Pattern(regexp = USERNAME_PATTERN, message = "Username should contains only letters, numbers and '_' sign!")
    @Length(
            min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH,
            message = "Username length should be between {min} and {max}!"
    )
    private String username;
    @NotNull
    @Length(
            min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH,
            message = "Password length should be between {min} and {max}!"
    )
    private String password;
    @Email(message = "Wrong email format!")
    @NotNull(message = "Email is required!")
    private String email;
    private Gender gender;

    public UserCreateForm() {
    }

    public UserCreateForm(String username, String password, String email, Gender gender) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = gender;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }
}
