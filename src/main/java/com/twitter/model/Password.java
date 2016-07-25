package com.twitter.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 14.07.16.
 */
@Entity
public class Password extends AbstractEntity {
    @NotNull
    @Length(
            min = 6, max = 10,
            message = "Password length should be between {min} and {max}!"
    )
    private String password;
    @NotNull
    private Date passwordExpireDate;

    public Password() {
        super();
        this.passwordExpireDate = Calendar.getInstance().getTime();
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

    public Date getPasswordExpireDate() {
        return passwordExpireDate;
    }

    public void setPasswordExpireDate(Date passwordExpireDate) {
        this.passwordExpireDate = passwordExpireDate;
    }
}
