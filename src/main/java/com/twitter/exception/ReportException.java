package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 03.01.17.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReportException extends TwitterException {
    public ReportException() {
        super();
    }

    public ReportException(String message) {
        super(message);
    }
}
