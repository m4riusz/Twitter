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
    public static final String SELECT_NEWEST_TWEETS_FROM_FOLLOWERS = "SELECT tweet FROM Tweet tweet WHERE tweet.owner IN (" + GET_USER_FOLLOWING_BY_ID + ") ORDER BY tweet.createDate DESC";

    public static final String SELECT_MOST_POPULAR_COMMENTS_FROM_TWEET = "SELECT comment FROM Comment comment WHERE comment.tweet.id = ?1 ORDER BY comment.votes.size DESC";

    public static final String SELECT_FAVOURITE_TWEETS_FROM_USER = "SELECT user.favouriteTweets FROM User user WHERE user.id = ?1";
    public static final String TWEET_EXISTS_IN_USER_FAVOURITES_TWEETS = "SELECT count(user) > 0 FROM User user JOIN user.favouriteTweets favTweets WHERE user.id = ?1 AND favTweets.id = ?2";
    public static final String TAG_ALREADY_IN_FAVOURITES_ERROR_MSG = "You already have this tag in yours favourite tags!";
}
