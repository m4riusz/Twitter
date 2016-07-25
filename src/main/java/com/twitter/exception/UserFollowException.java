package com.twitter.exception;

/**
 * Created by mariusz on 15.07.16.
 */
public class UserFollowException extends TwitterException {
    public UserFollowException() {
        super();
    }

    public UserFollowException(String message) {
        super(message);
    }
}
