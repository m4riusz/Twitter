package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 13.08.16.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TwitterDateException extends TwitterException {
    public TwitterDateException() {
        super();
    }

    public TwitterDateException(String message) {
        super(message);
    }
}
