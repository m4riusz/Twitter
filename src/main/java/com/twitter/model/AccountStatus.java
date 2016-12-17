package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.twitter.util.MessageUtil;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by mariusz on 31.07.16.
 */
@Entity
public class AccountStatus extends AbstractEntity {

    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enable;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = MessageUtil.DATE_FORMAT)
    private Date enableDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = MessageUtil.DATE_FORMAT)
    private Date bannedUntil;
    @NotNull
    @JsonIgnore
    private String verifyKey;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean deleted;

    public AccountStatus(boolean enable, String verifyKey) {
        this();
        this.enable = enable;
        this.verifyKey = verifyKey;
    }

    public AccountStatus() {
        this.enable = false;
        this.verifyKey = "";
        this.deleted = false;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
