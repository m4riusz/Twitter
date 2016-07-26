package com.twitter.route;

/**
 * Created by mariusz on 18.07.16.
 */
public final class Route {

    public static final String API = "/api";
    public static final String REST_USER = "/user";
    public static final String REST_USER_ID = "/user/{userId}";
    public static final String REST_USER_GET_ALL = "/user/{page}/{size}";


    public static final String LOGIN_URL = API + "/login";
    public static final String REGISTER_USER = API + REST_USER;
    public static final String LOGOUT_URL = API + "/logout";
}
