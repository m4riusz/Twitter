package com.twitter.dao;

import com.twitter.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.UserBuilder.user;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 19.07.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void findFollowersByUserId_noFollowers() {
        User user = a(user());
        userDao.save(user);
        List<User> userFollowers = userDao.findFollowersByUserId(user.getId(), new PageRequest(0, 10));
        assertThat(userFollowers, is(emptyList()));
    }

    @Test
    public void findFollowersByUserId_someFollowers() {
        User followerOne = a(user().withUsername("followerOne"));
        User followerTwo = a(user().withUsername("followerTwo"));
        User user = a(user().withFollowers(aListWith(followerOne, followerTwo)));
        userDao.save(aListWith(followerOne, followerTwo, user));
        List<User> userFollowers = userDao.findFollowersByUserId(user.getId(), new PageRequest(0, 10));
        assertThat(userFollowers, hasItems(followerOne, followerTwo));
    }

    @Test
    public void findFollowersByUserId_someFollowersAndSomeUsers() {
        User followerOne1 = a(user().withUsername("followerOne1"));
        User followerOne2 = a(user().withUsername("followerOne2"));
        User followerTwo = a(user().withUsername("followerTwo"));
        User userOne = a(user().withUsername("user1").withFollowers(aListWith(followerOne1, followerOne2)));
        User userTwo = a(user().withUsername("user2").withFollowers(aListWith(followerTwo)));
        userDao.save(aListWith(followerOne1, followerOne2, followerTwo, userOne, userTwo));
        List<User> userOneFollowers = userDao.findFollowersByUserId(userOne.getId(), new PageRequest(0, 10));
        List<User> userTwoFollowers = userDao.findFollowersByUserId(userTwo.getId(), new PageRequest(0, 10));
        assertThat(userOneFollowers, hasItems(followerOne1, followerOne2));
        assertThat(userTwoFollowers, hasItem(followerTwo));
    }

    @Test
    public void findFollowersCountByUserId_noFollowers() {
        User user = a(user());
        userDao.save(user);
        long followersCount = userDao.findFollowersCountByUserId(user.getId());
        assertThat(followersCount, is(equalTo(0L)));
    }


    @Test
    public void findFollowersCountByUserId_someFollowers() {
        User follower1 = a(user().withUsername("follower1"));
        User follower2 = a(user().withUsername("follower2"));
        User user = a(user().withUsername("User1").withFollowers(aListWith(follower1, follower2)));
        userDao.save(aListWith(follower1, follower2, user));
        long followersCount = userDao.findFollowersCountByUserId(user.getId());
        assertThat(followersCount, is(equalTo(2L)));
    }


    @Test
    public void findFollowersCountByUserId_someFollowersAndUsers() {
        User follower1 = a(user().withUsername("follower1"));
        User follower2 = a(user().withUsername("follower2"));
        User follower3 = a(user().withUsername("follower3"));
        User userOne = a(user().withUsername("User1").withFollowers(aListWith(follower1, follower2)));
        User userTwo = a(user().withUsername("User2").withFollowers(aListWith(follower3)));
        User userThree = a(user().withUsername("User3").withFollowers(emptyList()));
        userDao.save(aListWith(follower1, follower2, follower3, userOne, userTwo, userThree));
        long userOneFollowersCount = userDao.findFollowersCountByUserId(userOne.getId());
        long userTwoFollowersCount = userDao.findFollowersCountByUserId(userTwo.getId());
        long userThreeFollowersCount = userDao.findFollowersCountByUserId(userThree.getId());
        assertThat(userOneFollowersCount, is(equalTo(2L)));
        assertThat(userTwoFollowersCount, is(equalTo(1L)));
        assertThat(userThreeFollowersCount, is(equalTo(0L)));
    }

}
