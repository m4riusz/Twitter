package com.twitter.matchers;

import com.twitter.model.Result;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by mariusz on 29.07.16.
 */
public class ResultIsSuccessMatcher extends TypeSafeMatcher<Result> {


    @Override
    protected boolean matchesSafely(Result result) {
        return result.isSuccess();
    }

    @Override
    protected void describeMismatchSafely(Result item, Description mismatchDescription) {
        mismatchDescription.appendText("a result was ").appendValue(Boolean.FALSE);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a result ").appendValue(Boolean.TRUE);
    }

    public static ResultIsSuccessMatcher hasFinishedSuccessfully() {
        return new ResultIsSuccessMatcher();
    }
}
