package com.twitter.util;

public final class MessageUtil {

    public static final String USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "User with this id does not exist!";
    public static final String FOLLOW_YOURSELF_ERROR_MSG = "You cant follow yourself!";
    public static final String FOLLOW_ALREADY_FOLLOWED_ERROR_MSG = "You are already following this user!";
    public static final String UNFOLLOW_YOURSELF_ERROR_MSG = "You cant unfollow yourself!";
    public static final String UNFOLLOW_UNFOLLOWED_ERROR_MSG = "You cant unfollow user who is not followed!";
    public static final String USER_ALREADY_EXISTS_USERNAME_ERROR_MSG = "User with this username already exists!";
    public static final String USER_ALREADY_EXISTS_EMAIL_ERROR_MSG = "User with this email already exists!";
    public static final String ACCOUNT_HAS_BEEN_ALREADY_ENABLED = "Account has been already enabled!";
    public static final String POST_ALREADY_VOTED = "You have already voted!";
    public static final String REPORT_NOT_FOUND_BY_ID_ERROR_MSG = "Report with this id does not exist!";
    public static final String INVALID_VERIFY_KEY = "Invalid verify key!";
    public static final String POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "Post with this id does not exist!";

    public static final String HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG = "Hours cant be less or equal 0!";
    public static final String EMAIL_SUBJECT = "Twitter Registration!";
    public static final String EMAIL_CONTENT = "Thank you for registration. Please click ling below to activate account!";
    public static final String EMAIL_FROM = "marr994@o2.pl";
    public static final String EMAIL_VERIFY_LINK = "http://localhost:8080/api/user/verify/";
    public static final String DATE_IS_NOT_SET = "Date is not set!";
    public static final String REPORT_DATE_IS_INVALID_ERROR_MSG = "Date until banned is invalid!";
    public static final String DELETE_ABSTRACT_POST_CONTENT = "[Content has been deleted by administration]";

    public static final String RESULT_SUCCESS_MESSAGE = "Ok";
    public static final String VOTE_DOES_NOT_EXIST_ERROR_MSG = "Vote does not exist!";
    public static final String VOTE_DELETE_ERROR_MSG = "You cant delete not yours vote!";
}