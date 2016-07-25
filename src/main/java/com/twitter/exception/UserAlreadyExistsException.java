package com.twitter.exception;

/**
 * Created by mariusz on 25.07.16.
 */
public class UserAlreadyExistsException extends TwitterException {
    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
