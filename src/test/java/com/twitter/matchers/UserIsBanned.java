package com.twitter.matchers;

import com.twitter.model.User;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by mariusz on 02.08.16.
 */
public class UserIsBanned extends TypeSafeMatcher<User> {

    private boolean bannedExpected;

    public UserIsBanned(boolean bannedExpected) {
        this.bannedExpected = bannedExpected;
    }

    @Override
    protected boolean matchesSafely(User user) {
        return user.isAccountNonLocked() == !bannedExpected;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a user banned status ").appendValue(bannedExpected);
    }

    @Override
    protected void describeMismatchSafely(User item, Description mismatchDescription) {
        mismatchDescription.appendText("a user banned status was ").appendValue(item.isAccountNonLocked());
    }

    public static UserIsBanned isBanned(boolean bannedExpected) {
        return new UserIsBanned(bannedExpected);
    }
}
