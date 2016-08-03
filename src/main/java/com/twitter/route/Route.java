package com.twitter.route;

/**
 * Created by mariusz on 18.07.16.
 */
public final class Route {

    public static final String API = "/api";
    public static final String USER = "/user";
    public static final String VERIFY = "/verify";
    public static final String ALL = "/**";
    public static final String TWEET = "/tweet";

    public static final String USER_BY_ID = API + USER + "/{userId}";
    public static final String USER_GET_ALL = API + USER + "/{page}/{size}";
    public static final String LOGIN_URL = API + "/login";
    public static final String REGISTER_USER = API + USER;
    public static final String LOGOUT_URL = API + "/logout";
    public static final String VERIFY_USER_URL = API + USER + VERIFY + "/{verifyKey}";
    public static final String VERIFY_USER_URL_REGEX = API + USER + VERIFY + ALL;
    public static final String ALL_REQUESTS_REGEX = API + ALL;


    public static final String TWEET_URL = API + TWEET;
    public static final String TWEET_BY_ID = API + TWEET + "/{tweetId}";
    public static final String TWEET_GET_ALL = API + TWEET + "/{page}/{size}";
}
