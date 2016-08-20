package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 20.08.16.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserVoteNotFoundException extends UserVoteException {
    public UserVoteNotFoundException() {
        super();
    }

    public UserVoteNotFoundException(String message) {
        super(message);
    }
}
