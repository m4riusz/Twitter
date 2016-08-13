package com.twitter.exception;

/**
 * Created by mariusz on 15.07.16.
 */
public class UserUnfollowException extends UserException {
    public UserUnfollowException() {
        super();
    }

    public UserUnfollowException(String message) {
        super(message);
    }
}
