package com.twitter.dto;

/**
 * Created by mariusz on 07.01.17.
 */
public class VerifyResult {

    private String message;

    public VerifyResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
