package com.twitter.exception;

/**
 * Created by mariusz on 14.07.16.
 */
public abstract class TwitterException extends RuntimeException {
    public TwitterException() {
        super();
    }

    public TwitterException(String message) {
        super(message);
    }
}
