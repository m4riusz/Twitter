package com.twitter.dao;

/**
 * Created by mariusz on 13.07.16.
 */
public final class Query {
    public static final String GET_USER_FOLLOWERS_BY_ID = "SELECT follower FROM User user JOIN user.followers follower WHERE user.id= ?1";
    public static final String GET_USER_FOLLOWING_BY_ID = "SELECT user FROM User user JOIN user.followers follower WHERE follower.id= ?1";
    public static final String GET_USER_FOLLOWERS_COUNT_BY_ID = "SELECT count(follower) FROM User user JOIN user.followers follower WHERE user.id= ?1";
    public static final String GET_USER_FOLLOWING_COUNT_BY_ID = "SELECT count(user) FROM User user JOIN user.followers follower WHERE follower.id= ?1";

    public static final String SELECT_NEWEST_TWEETS_FROM_FOLLOWERS = "SELECT tweet FROM Tweet tweet WHERE tweet.owner IN (" + GET_USER_FOLLOWING_BY_ID + ") ORDER BY tweet.createDate DESC";
    public static final String SELECT_TWEETS_WITH_TAGS_AND_AFTER_DATE_ORDER_BY_VOTES = "SELECT tw FROM Tweet tw LEFT JOIN tw.tags tw_tags LEFT JOIN tw.votes tw_votes WHERE tw_tags.text IN ?1 AND tw.createDate > ?2 GROUP BY tw.id, tw.createDate, tw.version, tw.banned, tw.deleted, tw.content, tw.owner ORDER BY MAX(tw_votes.vote) ASC, COUNT(tw_votes) DESC, tw.createDate DESC";
    public static final String SELECT_TWEETS_AFTER_DATE_ORDER_BY_VOTES ="SELECT tw FROM Tweet tw LEFT JOIN tw.votes tw_votes WHERE tw.createDate > ?1 GROUP BY tw.id, tw.createDate, tw.version, tw.banned, tw.deleted, tw.content, tw.owner ORDER BY MAX(tw_votes.vote) ASC, COUNT(tw_votes) DESC, tw.createDate DESC";

    public static final String SELECT_FAVOURITE_TWEETS_FROM_USER = "SELECT user.favouriteTweets FROM User user WHERE user.id = ?1";
    public static final String TWEET_EXISTS_IN_USER_FAVOURITES_TWEETS = "SELECT count(user) > 0 FROM User user JOIN user.favouriteTweets favTweets WHERE user.id = ?1 AND favTweets.id = ?2";
}
