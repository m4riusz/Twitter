package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date passwordExpireDate;

    public Password() {
        super();
        this.passwordExpireDate = DateTime.now().plusYears(1).toDate();
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
