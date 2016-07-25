package com.twitter.model;

/**
 * Created by mariusz on 25.07.16.
 */
public class Result<T> {
    private boolean success;
    private T value;

    public Result(boolean success, T value) {
        this.success = success;
        this.value = value;
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
}
