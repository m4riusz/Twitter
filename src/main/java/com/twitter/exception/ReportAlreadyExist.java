package com.twitter.exception;

/**
 * Created by mariusz on 22.11.16.
 */
public class ReportAlreadyExist extends TwitterException {

    public ReportAlreadyExist() {
        super();
    }

    public ReportAlreadyExist(String message) {
        super(message);
    }
}
