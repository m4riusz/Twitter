package com.twitter.controller;

import com.twitter.exception.TwitterException;
import com.twitter.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariusz on 25.07.16.
 */
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result<List<String>> processValidationError(MethodArgumentNotValidException ex) {
        List<String> result = new ArrayList<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            result.add(fieldError.getDefaultMessage());
        }
        return new Result<>(false, result);
    }

    @ExceptionHandler(TwitterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result<List<String>> processValidationError(TwitterException ex) {
        List<String> result = new ArrayList<>();
        result.add(ex.getMessage());
        return new Result<>(false, result);
    }
}
