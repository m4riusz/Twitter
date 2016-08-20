package com.twitter.exception;

/**
 * Created by mariusz on 25.07.16.
 */

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException() {
        super();
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
