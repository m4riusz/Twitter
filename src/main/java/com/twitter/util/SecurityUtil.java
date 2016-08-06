package com.twitter.util;

/**
 * Created by mariusz on 27.07.16.
 */
public class SecurityUtil {
    public static final String ADMIN_OR_MODERATOR = "hasAuthority('ADMIN') OR hasAuthority('MODERATOR')";
    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String AUTHENTICATED = "isAuthenticated()";
    public static final String PERSONAL_USAGE = "#userId == principal.id";
    public static final String POST_PERSONAL = "#post.owner.id == principal.id";
    public static final String PERSONAL_VOTE = "#userVote.user.id == principal.id";
}
