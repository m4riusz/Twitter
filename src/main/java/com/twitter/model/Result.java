package com.twitter.model;

import com.twitter.util.MessageUtil;

/**
 * Created by mariusz on 25.07.16.
 */
public class Result<T> {
    private boolean success;
    private String message;
    private T value;

    public Result(boolean success, String message, T value) {
        this.success = success;
        this.value = value;
        this.message = message;
    }

    public static <T> Result<T> ResultSuccess(T value) {
        return new Result<>(true, MessageUtil.RESULT_SUCCESS_MESSAGE, value);
    }

    public static <T> Result<T> ResultFailure(String message) {
        return new Result<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
