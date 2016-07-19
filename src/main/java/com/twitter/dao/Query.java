package com.twitter.dao;

/**
 * Created by mariusz on 13.07.16.
 */
public final class Query {
    public static final String GET_USER_FOLLOWERS_BY_ID = "SELECT follower FROM User user JOIN user.followers follower WHERE user.id= ?1";
    public static final String GET_USER_FOLLOWING_BY_ID = "SELECT user FROM User user JOIN user.followers follower WHERE follower.id= ?1";
    public static final String GET_USER_FOLLOWERS_COUNT_BY_ID = "SELECT count(follower) FROM User user JOIN user.followers follower WHERE user.id= ?1";
    public static final String GET_USER_FOLLOWING_COUNT_BY_ID = "SELECT count(user) FROM User user JOIN user.followers follower WHERE follower.id= ?1";

    public static final String GET_COMMENTS_FROM_TWEET_BY_ID = "SELECT tweet.comments FROM Tweet tweet WHERE tweet.id = ?1";

}
