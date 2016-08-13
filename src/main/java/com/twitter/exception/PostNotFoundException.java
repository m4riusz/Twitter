package com.twitter.exception;

/**
 * Created by mariusz on 13.08.16.
 */
public class PostNotFoundException extends TwitterException {
    public PostNotFoundException() {
        super();
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
