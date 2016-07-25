package com.twitter.exception;

/**
 * Created by mariusz on 14.07.16.
 */
public class UserNotFoundException extends TwitterException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
