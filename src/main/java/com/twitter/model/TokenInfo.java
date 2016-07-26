package com.twitter.model;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 26.07.16.
 */
public final class TokenInfo {

    private final Date created = Calendar.getInstance().getTime();
    private final String token;
    private final UserDetails userDetails;

    public TokenInfo(String token, UserDetails userDetails) {
        this.token = token;
        this.userDetails = userDetails;
    }

    public String getToken() {
        return token;
    }

}