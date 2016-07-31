package com.twitter.model;

import com.sun.istack.internal.Nullable;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by mariusz on 31.07.16.
 */
@Entity
public class AccountStatus extends AbstractEntity {

    @NotNull
    private boolean enable;
    @Nullable
    private Date enableDate;
    @Nullable
    private Date bannedUntil;
    @NotNull
    private String verifyKey;

    public AccountStatus(boolean enable, String verifyKey) {
        this.enable = enable;
        this.verifyKey = verifyKey;
    }

    public AccountStatus() {
        this.enable = false;
        this.verifyKey = "";
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Date getEnableDate() {
        return enableDate;
    }

    public void setEnableDate(Date enableDate) {
        this.enableDate = enableDate;
    }

    public Date getBannedUntil() {
        return bannedUntil;
    }

    public void setBannedUntil(Date bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public String getVerifyKey() {
        return verifyKey;
    }

    public void setVerifyKey(String verifyKey) {
        this.verifyKey = verifyKey;
    }
}
