package com.twitter.util;

/**
 * Created by mariusz on 02.12.16.
 */
public final class Config {
    public static final int MAX_USER_NOTIFICATION_IN_ONE_POST = 20;

    public static final int MIN_POST_TEXT_LENGTH = 1;
    public static final int MAX_POST_TEXT_LENGTH = 300;

    public static final int MIN_AVATAR_FILENAME_LENGTH = 1;
    public static final int MAX_AVATAR_FILENAME_LENGTH = 100;

    public static final int MIN_REPORT_TEXT_LENGTH = 0;
    public static final int MAX_REPORT_TEXT_LENGTH = 100;

    public static final int MIN_TAG_TEXT_LENGTH = 1;
    public static final int MAX_TAG_TEXT_LENGTH = 50;

    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_USERNAME_LENGTH = 20;
}
