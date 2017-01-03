package com.twitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz on 22.11.16.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ReportAlreadyExist extends ReportException {

    public ReportAlreadyExist() {
        super();
    }

    public ReportAlreadyExist(String message) {
        super(message);
    }
}
