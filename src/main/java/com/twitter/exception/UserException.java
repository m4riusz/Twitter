package com.twitter.exception;

/**
 * Created by mariusz on 13.08.16.
 */
public class UserException extends TwitterException {
    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }
}
