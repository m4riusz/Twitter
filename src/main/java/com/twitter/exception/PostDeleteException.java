package com.twitter.exception;

/**
 * Created by mariusz on 13.08.16.
 */
public class PostDeleteException extends TwitterException {
    public PostDeleteException() {
        super();
    }

    public PostDeleteException(String message) {
        super(message);
    }
}
