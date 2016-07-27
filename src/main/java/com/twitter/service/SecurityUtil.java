package com.twitter.service;

/**
 * Created by mariusz on 27.07.16.
 */
public class SecurityUtil {
    public static final String ADMIN_OR_MODERATOR = "hasAuthority('ADMIN') OR hasAuthority('MODERATOR')";
    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String AUTHENTICATED = "isAuthenticated()";
}
