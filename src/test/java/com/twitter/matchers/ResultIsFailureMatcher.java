package com.twitter.matchers;


import com.twitter.model.Result;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by mariusz on 01.08.16.
 */
public class ResultIsFailureMatcher extends TypeSafeMatcher<Result> {


    @Override
    protected boolean matchesSafely(Result result) {
        return !result.isSuccess();
    }

    @Override
    protected void describeMismatchSafely(Result item, Description mismatchDescription) {
        mismatchDescription.appendText("a result was ").appendValue(true);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a result ").appendValue(false);
    }

    public static ResultIsFailureMatcher hasFailed() {
        return new ResultIsFailureMatcher();
    }
}