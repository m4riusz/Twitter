package com.twitter.route;

/**
 * Created by mariusz on 18.07.16.
 */
public final class Route {

    private static final String API = "/api";
    private static final String ALL = "/**";
    private static final String SIZE = "/{size}";
    private static final String PAGE = "/{page}";
    private static final String USER = "/user";
    private static final String USER_ID = "/{userId}";
    private static final String TWEET = "/tweet";
    private static final String TWEET_ID = "/{tweetId}";
    private static final String COMMENT = "/comment";
    private static final String COMMENT_ID = "/{commentId}";
    private static final String FOLLOW = "/follow";
    private static final String FOLLOWING = "/following";
    private static final String FOLLOWERS = "/followers";
    private static final String COUNT = "/count";
    private static final String VERIFY = "/verify";
    private static final String LATEST = "/latest";
    private static final String OLDEST = "/oldest";
    private static final String POPULAR = "/popular";
    private static final String VOTE = "/vote";

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
    public static final String TWEETS_MOST_VOTED = API + TWEET + POPULAR + PAGE + SIZE;
    public static final String TWEET_VOTE = API + TWEET + TWEET_ID + VOTE;


    public static final String TWEETS_WITH_TAGS = API + TWEET + "/tags" + PAGE + SIZE;
    public static final String COMMENT_URL = API + COMMENT;
    public static final String COMMENTS_FROM_TWEET = API + TWEET + TWEET_ID + COMMENT + PAGE + SIZE;
    public static final String COMMENT_BY_ID = API + COMMENT + COMMENT_ID;
    public static final String COMMENTS_LATEST = API + TWEET + TWEET_ID + COMMENT + LATEST + PAGE + SIZE;
    public static final String COMMENTS_OLDEST = API + TWEET + TWEET_ID + COMMENT + OLDEST + PAGE + SIZE;
    public static final String COMMENTS_POPULAR = API + TWEET + TWEET_ID + COMMENT + POPULAR + PAGE + SIZE;
}
