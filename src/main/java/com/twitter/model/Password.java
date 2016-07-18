package com.twitter.model;

import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 14.07.16.
 */
@Entity
public class Password extends AbstractEntity {
    @NotNull
    private String password;
    @NotNull
    private DateTime passwordExpireDate;

    public Password() {
        super();
        this.passwordExpireDate = DateTime.now();
    }

    public Password(String password) {
        this();
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DateTime getPasswordExpireDate() {
        return passwordExpireDate;
    }

    public void setPasswordExpireDate(DateTime passwordExpireDate) {
        this.passwordExpireDate = passwordExpireDate;
    }
}
