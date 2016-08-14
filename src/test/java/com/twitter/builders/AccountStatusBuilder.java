package com.twitter.builders;

import com.twitter.model.AccountStatus;
import com.twitter.util.Builder;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 14.08.16.
 */
public final class AccountStatusBuilder implements Builder<AccountStatus> {
    private static long counter = 0L;
    private String verifyKey = "verifyKey_" + counter;
    private boolean enable = true;
    private Date enableDate = Calendar.getInstance().getTime();
    private Date bannedUntil = null;
    private long id;
    private Date createDate = Calendar.getInstance().getTime();

    public static AccountStatusBuilder accountStatus() {
        counter++;
        return new AccountStatusBuilder();
    }

    public AccountStatusBuilder withVerifyKey(String verifyKey) {
        this.verifyKey = verifyKey;
        return this;
    }

    public AccountStatusBuilder withEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public AccountStatusBuilder withEnableDate(Date enableDate) {
        this.enableDate = enableDate;
        return this;
    }

    public AccountStatusBuilder withBannedUntil(Date bannedUntil) {
        this.bannedUntil = bannedUntil;
        return this;
    }

    public AccountStatusBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public AccountStatusBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public AccountStatus build() {
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setVerifyKey(verifyKey);
        accountStatus.setEnable(enable);
        accountStatus.setEnableDate(enableDate);
        accountStatus.setBannedUntil(bannedUntil);
        accountStatus.setId(id);
        accountStatus.setCreateDate(createDate);
        return accountStatus;
    }
}
