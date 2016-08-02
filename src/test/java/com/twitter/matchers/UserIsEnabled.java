package com.twitter.matchers;

import com.twitter.model.User;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by mariusz on 02.08.16.
 */
public class UserIsEnabled extends TypeSafeMatcher<User> {

    private boolean enabledExpected;

    public UserIsEnabled(boolean enabledExpected) {
        this.enabledExpected = enabledExpected;
    }

    @Override
    protected boolean matchesSafely(User user) {
        return user.isEnabled() == enabledExpected;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a user enable status ").appendValue(enabledExpected);
    }

    @Override
    protected void describeMismatchSafely(User item, Description mismatchDescription) {
        mismatchDescription.appendText("a user enable status was ").appendValue(item.isEnabled());
    }

    public static UserIsEnabled isEnabled(boolean bannedExpected) {
        return new UserIsEnabled(bannedExpected);
    }
}