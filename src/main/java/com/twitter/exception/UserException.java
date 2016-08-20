package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 13.08.16.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserException extends TwitterException {
    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }
}
