package com.twitter.exception;

/**
 * Created by mariusz on 29.07.16.
 */
public class TweetCreateException extends TwitterException {

    public TweetCreateException() {
        super();
    }

    public TweetCreateException(String message) {
        super(message);
    }
}
