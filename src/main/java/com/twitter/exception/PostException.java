package com.twitter.exception;

/**
 * Created by mariusz on 17.08.16.
 */
public class PostException extends TwitterException {
    public PostException() {
        super();
    }

    public PostException(String message) {
        super(message);
    }
}
