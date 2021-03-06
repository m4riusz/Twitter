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
    private static final String USERNAME = "/{username}";
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
    private static final String VOTE_ID = "/{voteId}";
    private static final String REPORT = "/report";
    private static final String REPORT_ID = "/{reportId}";
    private static final String STATUS = "/status";
    private static final String CATEGORY = "/category";
    private static final String REPORT_STATUS = "/{reportStatus}";
    private static final String REPORT_CATEGORY = "/{reportCategory}";
    private static final String AVATAR = "/avatar";
    private static final String FAVOURITES = "/favourites";
    private static final String TAGS = "/tags";
    private static final String HOURS = "/{hours}";
    private static final String PASSWORD = "/password";
    private static final String ROLE = "/role";
    private static final String EMAIL = "/email";
    private static final String NOTIFICATION = "/notification";
    private static final String NOTIFICATION_ID = "/{notificationId}";
    private static final String SEEN = "/{seen}";
    private static final String QUERY = "/query";


    public static final String LOGIN_URL = API + "/login";
    public static final String LOGOUT_URL = API + "/logout";
    public static final String ALL_REQUESTS_REGEX = API + ALL;
    public static final String VERIFY_USER_URL = API + USER + VERIFY + "/{verifyKey}";
    public static final String VERIFY_USER_URL_REGEX = API + USER + VERIFY + ALL;

    public static final String USER_URL = API + USER;
    public static final String USER_BY_ID = USER_URL + USER_ID;
    public static final String USER_PASSWORD_CHANGE = USER_URL + USER_ID + PASSWORD;
    public static final String USER_ROLE_CHANGE = USER_URL + USER_ID + ROLE;
    public static final String USER_EMAIL_CHANGE = USER_BY_ID + EMAIL;
    public static final String USER_FOLLOW = USER_URL + FOLLOW + USER_ID;
    public static final String USER_GET_ALL = USER_URL + PAGE + SIZE;
    public static final String USER_COUNT_GET_ALL = USER_URL + COUNT;
    public static final String REGISTER_USER = USER_URL;
    public static final String USER_WITH_USERNAME_LIKE = USER_URL + QUERY + USERNAME + PAGE + SIZE;
    public static final String USER_GET_FOLLOWERS = USER_BY_ID + FOLLOWERS + PAGE + SIZE;
    public static final String USER_COUNT_GET_FOLLOWERS = USER_BY_ID + FOLLOWERS + COUNT;
    public static final String USER_GET_FOLLOWING = USER_BY_ID + FOLLOWING + PAGE + SIZE;
    public static final String USER_COUNT_GET_FOLLOWING = USER_BY_ID + FOLLOWING + COUNT;
    public static final String USER_AVATAR = USER_BY_ID + AVATAR;
    public static final String USER_FAVOURITE_TAGS = USER_BY_ID + TAGS;

    public static final String TAG_WITH_TEXT_LIKE = API + TAGS + QUERY + "/{tagText}" + PAGE + SIZE;

    public static final String TWEET_URL = API + TWEET;
    public static final String TWEET_BY_ID = TWEET_URL + TWEET_ID;
    public static final String TWEET_GET_ALL = TWEET_URL + PAGE + SIZE;
    public static final String TWEETS_FROM_FOLLOWINGS_USERS = TWEET_URL + "/my" + USER_ID + PAGE + SIZE;
    public static final String TWEETS_FROM_USER = TWEET_URL + USER + USER_ID + PAGE + SIZE;
    public static final String TWEETS_MOST_VOTED = TWEET_URL + POPULAR + HOURS + PAGE + SIZE;
    public static final String TWEET_VOTE = TWEET_URL + VOTE;
    public static final String TWEET_USER_VOTE = TWEET_BY_ID + VOTE;
    public static final String TWEET_USER_VOTE_COUNT = TWEET_BY_ID + COUNT + VOTE + VOTE_ID;
    public static final String TWEETS_WITH_TAGS = TWEET_URL + TAGS + "/{tags}" + PAGE + SIZE;
    public static final String TWEETS_WITH_TAGS_POPULAR = TWEET_URL + TAGS + "/{tags}" + POPULAR + HOURS + PAGE + SIZE;
    public static final String TWEETS_FROM_USER_FAVOURITES = USER_BY_ID + FAVOURITES + PAGE + SIZE;
    public static final String TWEET_TO_USER_FAVOURITES = USER_URL + FAVOURITES + TWEET_ID;

    public static final String COMMENT_URL = API + COMMENT;
    public static final String COMMENTS_FROM_TWEET = TWEET_BY_ID + COMMENT + PAGE + SIZE;
    public static final String COMMENT_BY_ID = COMMENT_URL + COMMENT_ID;
    public static final String COMMENTS_FROM_USER = COMMENT_URL + USER + USER_ID + PAGE + SIZE;
    public static final String COMMENTS_LATEST = TWEET_BY_ID + COMMENT + LATEST + PAGE + SIZE;
    public static final String COMMENTS_OLDEST = TWEET_BY_ID + COMMENT + OLDEST + PAGE + SIZE;
    public static final String COMMENTS_POPULAR = TWEET_BY_ID + COMMENT + POPULAR + PAGE + SIZE;
    public static final String COMMENT_VOTE = COMMENT_URL + VOTE;
    public static final String COMMENT_VOTE_BY_COMMENT_ID = COMMENT_BY_ID + VOTE;
    public static final String COMMENT_USER_VOTE_COUNT = COMMENT_BY_ID + COUNT + VOTE + VOTE_ID;


    public static final String REPORT_URL = API + REPORT;
    public static final String REPORT_GET_LATEST = REPORT_URL + LATEST + PAGE + SIZE;
    public static final String REPORT_GET_FROM_USER = REPORT_URL + PAGE + SIZE;
    public static final String REPORT_BY_ID = REPORT_URL + REPORT_ID;
    public static final String REPORT_GET_ALL_BY_STATUS = REPORT_URL + STATUS + REPORT_STATUS + PAGE + SIZE;
    public static final String REPORT_GET_ALL_BY_CATEGORY = REPORT_URL + CATEGORY + REPORT_CATEGORY + PAGE + SIZE;
    public static final String REPORT_GET_ALL_BY_STATUS_AND_CATEGORY = REPORT_URL + STATUS + REPORT_STATUS +
            CATEGORY + REPORT_CATEGORY + PAGE + SIZE;

    public static final String NOTIFICATION_URL = API + NOTIFICATION;
    public static final String NOTIFICATION_BY_ID = NOTIFICATION_URL + NOTIFICATION_ID;
    public static final String NOTIFICATION_GET = NOTIFICATION_URL + SEEN + PAGE + SIZE;

}
