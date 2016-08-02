package com.twitter.matchers;

import com.twitter.model.Result;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by mariusz on 29.07.16.
 */
public class ResultValueMatcher<T> extends TypeSafeMatcher<Result<T>> {

    private T expectedValue;

    public ResultValueMatcher(T expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    protected boolean matchesSafely(Result<T> tResult) {
        return tResult.getValue().equals(expectedValue);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("result has value of ").appendValue(expectedValue);
    }

    @Override
    protected void describeMismatchSafely(Result<T> item, Description mismatchDescription) {
        mismatchDescription.appendText("result has value of ").appendValue(item.getValue());
    }

    public static <T> ResultValueMatcher<T> hasValueOf(T value) {
        return new ResultValueMatcher<>(value);
    }
}
