package com.twitter.dao;

/**
 * Created by mariusz on 13.07.16.
 */
public final class Query {
    public static final String GET_USER_FOLLOWERS_BY_ID = "SELECT follower FROM User user JOIN user.followers follower WHERE user.id= ?1";
    public static final String GET_USER_FOLLOWING_BY_ID = "SELECT user FROM User user JOIN user.followers follower WHERE follower.id= ?1";
    public static final String GET_USER_FOLLOWERS_COUNT_BY_ID = "SELECT count(follower) FROM User user JOIN user.followers follower WHERE user.id= ?1";
    public static final String GET_USER_FOLLOWING_COUNT_BY_ID = "SELECT count(user) FROM User user JOIN user.followers follower WHERE follower.id= ?1";

    public static final String SELECT_MOST_POPULAR_TWEETS_BY_TIME = "SELECT tweet FROM Tweet tweet WHERE ((day(current_timestamp) * 24 + hour(current_timestamp)) - (day(tweet.createDate) * 24 + hour(tweet.createDate))) < ?1 ORDER BY tweet.votes.size DESC";

}
