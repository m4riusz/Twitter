package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 17.08.16.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PostException extends TwitterException {
    public PostException() {
        super();
    }

    public PostException(String message) {
        super(message);
    }
}
