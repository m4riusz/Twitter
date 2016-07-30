package com.twitter.service;

public final class MessageUtil {
    public static final String USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "User with this is does not exist!";
    public static final String FOLLOW_YOURSELF_ERROR_MSG = "You cant follow yourself!";
    public static final String FOLLOW_ALREADY_FOLLOWED_ERROR_MSG = "You are already following this user!";
    public static final String UNFOLLOW_YOURSELF_ERROR_MSG = "You cant unfollow yourself!";
    public static final String UNFOLLOW_UNFOLLOWED_ERROR_MSG = "You cant unfollow user who is not followed!";
    public static final String USER_ALREADY_EXISTS_ERROR_MSG = "User with this username already exists!";

    public static final String TWEET_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "Tweet with this id does not exist!";
    public static final String USER_OR_TWEET_IS_NULL_MSG = "User or tweet is undefined!";
    public static final String HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG = "Hours cant be less or equal 0!";
}