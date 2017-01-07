package com.twitter.util;

public final class MessageUtil {

    public static final String USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "User with this id does not exists!";
    public static final String USER_DOES_NOT_EXISTS_BY_USERNAME_ERROR_MSG = "User with this username does not exists!";
    public static final String FOLLOW_YOURSELF_ERROR_MSG = "You cant follow yourself!";
    public static final String FOLLOW_ALREADY_FOLLOWED_ERROR_MSG = "You are already following this user!";
    public static final String UNFOLLOW_YOURSELF_ERROR_MSG = "You cant unfollow yourself!";
    public static final String UNFOLLOW_UNFOLLOWED_ERROR_MSG = "You cant unfollow user who is not followed!";
    public static final String USER_ALREADY_EXISTS_USERNAME_ERROR_MSG = "User with this username already exists!";
    public static final String USER_ALREADY_EXISTS_EMAIL_ERROR_MSG = "User with this email already exists!";
    public static final String USER_SAME_PASSWORD_ERROR_MSG = "Your new password should be different!";
    public static final String USER_SAME_EMAIL_CHANGE_ERROR_MSG = "Your new email should be different!";
    public static final String ACCOUNT_HAS_BEEN_ENABLED = "You have successfully enabled account!";
    public static final String ACCOUNT_HAS_BEEN_ALREADY_ENABLED = "Account has been already enabled!";
    public static final String REPORT_NOT_FOUND_BY_ID_ERROR_MSG = "Report with this id does not exists!";
    public static final String REPORT_ALREADY_EXISTS = "You have already reported this post!";
    public static final String REPORT_JUDGE_SELF_EXCEPTION = "You can't judge this report, because it is related to you!";
    public static final String INVALID_VERIFY_KEY = "Invalid verify key!";

    public static final String POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG = "Post with this id does not exists!";
    public static final String HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG = "Hours cant be less or equal 0!";
    public static final String EMAIL_SUBJECT = "Twitter v2";
    public static final String EMAIL_FROM = "tw_it@o2.pl";
    public static final String EMAIL_VERIFY_LINK = "http://localhost:8080/#/verify/";
    public static final String DATE_IS_NOT_SET = "Date is not set!";
    public static final String REPORT_DATE_IS_INVALID_ERROR_MSG = "Date until banned is invalid!";
    public static final String DELETE_ABSTRACT_POST_CONTENT = "[Content has been deleted by administration]";

    public static final String DELETE_BY_OWNED_ABSTRACT_POST_CONTENT = "[Content has been deleted by owner]";
    public static final String VOTE_DOES_NOT_EXIST_ERROR_MSG = "Vote does not exists!";
    public static final String VOTE_DELETE_ERROR_MSG = "You cant delete not yours vote!";
    public static final String DELETE_NOT_OWN_POST = "You cant delete not yours posts!";
    public static final String POST_ALREADY_DELETED = "Post is already deleted!";
    public static final String POST_ALREADY_IN_FAVOURITES_ERROR_MSG = "Post is already in favourites!";

    public static final String POST_DOES_NOT_BELONG_TO_FAVOURITES_TWEETS_ERROR_MSG = "Post does not belong to yours favourite tweets!";
    public static final String TAG_ALREADY_IN_FAVOURITES_ERROR_MSG = "You already have this tag in yours favourite tags!";
    public static final String TAG_NOT_IN_FAVOURITES_DELETE_ERROR_MSG = "You cant remove tag, because it is not in your favourite tags!";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String YOU_HAVE_BEEN_MENTIONED_MESSAGE = "You have been mentioned by @";
    public static final String NOTIFICATION_DOES_NOT_EXISTS = "Notification does not exist!";
    public static final String ACCESS_DENIED = "Access denied";
}