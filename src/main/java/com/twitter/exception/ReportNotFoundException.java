package com.twitter.exception;

/**
 * Created by mariusz on 31.07.16.
 */
public class ReportNotFoundException extends TwitterException {
    public ReportNotFoundException(String message) {
        super(message);
    }

    public ReportNotFoundException() {
        super();
    }
}
