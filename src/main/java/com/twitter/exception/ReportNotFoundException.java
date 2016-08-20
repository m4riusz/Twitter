package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 31.07.16.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends TwitterException {
    public ReportNotFoundException(String message) {
        super(message);
    }

    public ReportNotFoundException() {
        super();
    }
}
