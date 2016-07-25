package com.twitter.exception;

/**
 * Created by mariusz on 15.07.16.
 */
public class UserUnfollowException extends TwitterException {
    public UserUnfollowException() {
        super();
    }

    public UserUnfollowException(String message) {
        super(message);
    }
}
