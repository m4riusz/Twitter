package com.twitter.service;

public final class MessageUtil {

    public static final String USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "User with this is does not exist!";
    public static final String FOLLOW_YOURSELF_ERROR_MSG = "You cant follow yourself!";
    public static final String FOLLOW_ALREADY_FOLLOWED_ERROR_MSG = "You are already following this user!";
    public static final String UNFOLLOW_YOURSELF_ERROR_MSG = "You cant unfollow yourself!";
    public static final String UNFOLLOW_UNFOLLOWED_ERROR_MSG = "You cant unfollow user who is not followed!";
    public static final String USER_ALREADY_EXISTS_USERNAME_ERROR_MSG = "User with this username already exists!";
    public static final String USER_ALREADY_EXISTS_EMAIL_ERROR_MSG = "User with this email already exists!";
    public static final String ACCOUNT_HAS_BEEN_ENABLED = "Account has been enabled!";
    public static final String ACCOUNT_HAS_BEEN_ALREADY_ENABLED = "Account has been already enabled!";

    public static final String INVALID_VERIFY_KEY = "Invalid verify key!";
    public static final String TWEET_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "Tweet with this id does not exist!";
    public static final String USER_OR_TWEET_IS_NULL_MSG = "User or tweet is undefined!";

    public static final String REPORT_NOT_FOUND_BY_ID_ERROR_MSG = "Report with this id does not exist!";


    public static final String HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG = "Hours cant be less or equal 0!";
    public static final String EMAIL_SUBJECT = "Twitter Registration!";
    public static final String EMAIL_CONTENT = "Thank you for registration. Please click ling below to activate account!";
    public static final String EMAIL_FROM = "some@email.com";
    public static final String EMAIL_VERIFY_LINK = "http://localhost:8080/api/user/verify/";
    public static final String REPORT_DATE_NOT_SET_ERROR_MSG = "Report date is not set!";
    public static final String REPORT_DATE_IS_INVALID_ERROR_MSG = "Date until banned is invalid!";
    public static final String DELETE_ABSTRACT_POST_CONTENT = "[Content has been deleted by administration]";
}