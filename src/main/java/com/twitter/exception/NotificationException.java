package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 02.12.16.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotificationException extends TwitterException {
    public NotificationException() {
        super();
    }

    public NotificationException(String message) {
        super(message);
    }
}
