package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 02.12.16.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificationNotFound extends NotificationException {

    public NotificationNotFound() {
        super();
    }

    public NotificationNotFound(String message) {
        super(message);
    }
}
