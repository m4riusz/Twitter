package com.twitter.exception;

/**
 * Created by mariusz on 29.07.16.
 */
public class TweetNotFoundException extends TwitterException {

    public TweetNotFoundException() {
        super();
    }

    public TweetNotFoundException(String message) {
        super(message);
    }
}
