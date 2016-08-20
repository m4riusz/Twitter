package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 13.08.16.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserVoteException extends TwitterException {
    public UserVoteException() {
        super();
    }

    public UserVoteException(String message) {
        super(message);
    }
}

