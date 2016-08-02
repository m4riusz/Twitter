package com.twitter.matchers;

import com.twitter.model.Result;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by mariusz on 02.08.16.
 */
public class ResultMessageMatcher extends TypeSafeMatcher<Result> {

    private String expectedMessage;

    public ResultMessageMatcher(String expectedMessage) {
        this.expectedMessage = expectedMessage;
    }

    @Override
    protected boolean matchesSafely(Result result) {
        return result.getMessage().equals(expectedMessage);
    }

    @Override
    protected void describeMismatchSafely(Result item, Description mismatchDescription) {
        mismatchDescription.appendText("a result message was ").appendValue(item.getMessage());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a result message ").appendValue(expectedMessage);
    }

    public static ResultMessageMatcher hasMessageOf(String expectedMessage) {
        return new ResultMessageMatcher(expectedMessage);
    }
}
