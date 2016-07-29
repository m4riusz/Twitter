package com.twitter.matchers;

import com.twitter.model.User;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mariusz on 29.07.16.
 */
public class UserFollowerMatcher extends TypeSafeMatcher<User> {

    private List<User> followers;

    public UserFollowerMatcher(User... followers) {
        this.followers = Arrays.asList(followers);
    }

    @Override
    protected boolean matchesSafely(User user) {
        return user.getFollowers().containsAll(followers);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("user follower list with ").appendValue(followers);
    }

    @Override
    protected void describeMismatchSafely(User item, Description mismatchDescription) {
        mismatchDescription.appendText("user follower list with ").appendValue(item.getFollowers());
    }

    public static UserFollowerMatcher hasFollowers(User... users) {
        return new UserFollowerMatcher(users);
    }
}
