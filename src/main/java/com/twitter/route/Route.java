package com.twitter.route;

/**
 * Created by mariusz on 18.07.16.
 */
public final class Route {

    public static final String API = "/api";
    public static final String USER = "/user";
    public static final String VERIFY = "/verify";
    public static final String ALL = "/**";
    public static final String TWEET_ID = "/{tweetId}";
    public static final String SIZE = "/{size}";
    public static final String PAGE = "/{page}";
    public static final String USER_ID = "/{userId}";
    public static final String TWEET = "/tweet";
    public static final String FOLLOW = "/follow";
    public static final String FOLLOWING = "/following";
    public static final String FOLLOWERS = "/followers";
    public static final String COUNT = "/count";

    public static final String LOGIN_URL = API + "/login";
    public static final String LOGOUT_URL = API + "/logout";
    public static final String REGISTER_USER = API + USER;
    public static final String ALL_REQUESTS_REGEX = API + ALL;
    public static final String VERIFY_USER_URL = API + USER + VERIFY + "/{verifyKey}";
    public static final String VERIFY_USER_URL_REGEX = API + USER + VERIFY + ALL;

    public static final String USER_BY_ID = API + USER + USER_ID;
    public static final String USER_FOLLOW = API + USER + FOLLOW + USER_ID;
    public static final String USER_GET_ALL = API + USER + PAGE + SIZE;
    public static final String USER_COUNT_GET_ALL = API + USER + COUNT;
    public static final String USER_GET_FOLLOWERS = API + USER + USER_ID + FOLLOWERS + PAGE + SIZE;
    public static final String USER_COUNT_GET_FOLLOWERS = API + USER + USER_ID + FOLLOWERS + COUNT;
    public static final String USER_GET_FOLLOWING = API + USER + USER_ID + FOLLOWING + PAGE + SIZE;
    public static final String USER_COUNT_GET_FOLLOWING = API + USER + USER_ID + FOLLOWING + COUNT;



    public static final String TWEET_URL = API + TWEET;
    public static final String TWEET_BY_ID = API + TWEET + TWEET_ID;
    public static final String TWEET_GET_ALL = API + TWEET + PAGE + SIZE;
    public static final String TWEETS_FROM_FOLLOWINGS_USERS = API + TWEET + "/my" + PAGE + SIZE;
    public static final String TWEETS_FROM_USER = API + TWEET + USER + USER_ID;
    public static final String TWEETS_MOST_VOTED = API + TWEET + "/popular" + PAGE + SIZE;
    public static final String TWEETS_WITH_TAGS = API + TWEET + "/tags" + PAGE + SIZE;
}
