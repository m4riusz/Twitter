package com.twitter.dto;

import com.twitter.model.Gender;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 22.11.16.
 */
public class UserCreateForm {

    @NotNull
    @Length(
            min = 3, max = 10,
            message = "Username length should be between {min} and {max}!"
    )
    private String username;
    @NotNull
    @Length(
            min = 6, max = 10,
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
