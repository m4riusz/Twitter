package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 30.07.16.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TwitterGetException extends TwitterException {
    public TwitterGetException() {
        super();
    }

    public TwitterGetException(String message) {
        super(message);
    }
}
