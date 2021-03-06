package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 13.08.16.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends PostException {
    public PostNotFoundException() {
        super();
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
