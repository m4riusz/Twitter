package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.AccountStatus;
import com.twitter.model.User;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 19.07.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void findFollowersByUserId_noFollowers() {
        User user = a(user());
        userDao.save(user);
        List<User> userFollowers = userDao.findFollowersByUserId(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(userFollowers, is(emptyList()));
    }

    @Test
    public void findFollowersByUserId_someFollowers() {
        User followerOne = a(user());
        User followerTwo = a(user());
        User user = a(user()
                .withFollowers(
                        aListWith(
                                followerOne,
                                followerTwo
                        )
                )
        );
        userDao.save(aListWith(followerOne, followerTwo, user));
        List<User> userFollowers = userDao.findFollowersByUserId(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(userFollowers, hasItems(followerOne, followerTwo));
    }

    @Test
    public void findFollowersByUserId_someFollowersAndSomeUsers() {
        User followerOne1 = a(user());
        User followerOne2 = a(user());
        User followerTwo = a(user());
        User userOne = a(user()
                .withFollowers(
                        aListWith(
                                followerOne1,
                                followerOne2
                        )
                )
        );
        User userTwo = a(user().withFollowers(aListWith(followerTwo)));
        userDao.save(aListWith(followerOne1, followerOne2, followerTwo, userOne, userTwo));
        List<User> userOneFollowers = userDao.findFollowersByUserId(
                userOne.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        List<User> userTwoFollowers = userDao.findFollowersByUserId(
                userTwo.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(userOneFollowers, hasItems(followerOne1, followerOne2));
        assertThat(userTwoFollowers, hasItem(followerTwo));
    }

    @Test
    public void findFollowersCountByUserId_noFollowers() {
        User user = a(user());
        userDao.save(user);
        long followersCount = userDao.findFollowersCountByUserId(
                user.getId()
        );
        assertThat(followersCount, is(equalTo(0L)));
    }


    @Test
    public void findFollowersCountByUserId_someFollowers() {
        User follower1 = a(user());
        User follower2 = a(user());
        User user = a(user()
                .withFollowers(
                        aListWith(
                                follower1,
                                follower2
                        )
                )
        );
        userDao.save(aListWith(follower1, follower2, user));
        long followersCount = userDao.findFollowersCountByUserId(
                user.getId()
        );
        assertThat(followersCount, is(equalTo(2L)));
    }


    @Test
    public void findFollowersCountByUserId_someFollowersAndUsers() {
        User follower1 = a(user());
        User follower2 = a(user());
        User follower3 = a(user());
        User userOne = a(user()
                .withFollowers(
                        aListWith(
                                follower1,
                                follower2
                        )
                )
        );
        User userTwo = a(user()
                .withFollowers(
                        aListWith(
                                follower3
                        )
                )
        );
        User userThree = a(user()
                .withFollowers(
                        emptyList()
                )
        );
        userDao.save(aListWith(follower1, follower2, follower3, userOne, userTwo, userThree));

        long userOneFollowersCount = userDao.findFollowersCountByUserId(
                userOne.getId()
        );
        long userTwoFollowersCount = userDao.findFollowersCountByUserId(
                userTwo.getId()
        );
        long userThreeFollowersCount = userDao.findFollowersCountByUserId(
                userThree.getId()
        );
        assertThat(userOneFollowersCount, is(equalTo(2L)));
        assertThat(userTwoFollowersCount, is(equalTo(1L)));
        assertThat(userThreeFollowersCount, is(equalTo(0L)));
    }

    @Test
    public void findFollowingByUserId_noFollowings() {
        User user = a(user());
        userDao.save(aListWith(user));
        List<User> followingUsers = userDao.findFollowingByUserId(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(followingUsers, is(emptyList()));
    }

    @Test
    public void findFollowingByUserId_someFollowings() {
        User user = a(user());
        User following1 = a(user().
                withFollowers(
                        aListWith(user)
                )
        );
        User following2 = a(user()
                .withFollowers(
                        aListWith(
                                user
                        )
                )
        );
        userDao.save(aListWith(user, following1, following2));

        List<User> followingUsers = userDao.findFollowingByUserId(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(followingUsers, hasItems(following1, following2));
    }

    @Test
    public void findFollowingByUserId_someFollowingsAndUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        User following1 = a(user()
                .withFollowers(
                        aListWith(
                                userOne
                        )
                )
        );
        User following2 = a(user()
                .withFollowers(
                        aListWith(
                                userOne
                        )
                )
        );
        User following3 = a(user().
                withFollowers(
                        aListWith(
                                userTwo
                        )
                )
        );
        userDao.save(aListWith(userOne, userTwo, following1, following2, following3));
        List<User> userOneFollowings = userDao.findFollowingByUserId(
                userOne.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        List<User> userTwoFollowings = userDao.findFollowingByUserId(
                userTwo.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(userOneFollowings, hasItems(following1, following2));
        assertThat(userTwoFollowings, hasItem(following3));
    }

    @Test
    public void findFollowingCountByUserId_noFollowers() {
        User user = a(user());
        userDao.save(user);
        long followersCount = userDao.findFollowingCountByUserId(
                user.getId()
        );
        assertThat(followersCount, is(equalTo(0L)));
    }


    @Test
    public void findFollowingCountByUserId_someFollowers() {
        User user = a(user());
        User followingOne = a(user()
                .withFollowers(
                        aListWith(
                                user
                        )
                )
        );
        User followingTwo = a(user()
                .withFollowers(
                        aListWith(
                                user
                        )
                )
        );
        userDao.save(aListWith(user, followingOne, followingTwo));
        long followingCount = userDao.findFollowingCountByUserId(
                user.getId()
        );
        assertThat(followingCount, is(equalTo(2L)));
    }


    @Test
    public void findFollowingCountByUserId_someFollowersAndUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        User following1 = a(user()
                .withFollowers(
                        aListWith(
                                userOne
                        )
                )
        );
        User following2 = a(user()
                .withFollowers(
                        aListWith(
                                userOne
                        )
                )
        );
        User following3 = a(user()
                .withFollowers(
                        aListWith(
                                userOne,
                                userTwo
                        )
                )
        );
        userDao.save(aListWith(userOne, userTwo, following1, following2, following3));
        long userOneFollowingCount = userDao.findFollowingCountByUserId(
                userOne.getId()
        );
        long userTwoFollowingCount = userDao.findFollowingCountByUserId(
                userTwo.getId()
        );
        assertThat(userOneFollowingCount, is(equalTo(3L)));
        assertThat(userTwoFollowingCount, is(equalTo(1L)));
    }

    @Test
    public void findByUsername_userDoesNotExists() {
        User user = userDao.findByUsername("As");
        assertThat(user, is(nullValue()));
    }

    @Test
    public void findByUsername_userExists() {
        String username = "4chan";
        User user = a(user()
                .withUsername(username)
        );
        userDao.save(aListWith(user));
        User userFromDatabase = userDao.findByUsername(
                username
        );
        assertThat(userFromDatabase, is(user));
    }

    @Test
    public void findByEmail_emailDoesNotExist() {
        User userByEmail = userDao.findByEmail("some@email.com");
        assertThat(userByEmail, is(nullValue()));
    }

    @Test
    public void findByEmail_emailExist() {
        String email = "some@email.com";
        User user = a(user()
                .withEmail(email)
        );
        userDao.save(user);
        User userByEmail = userDao.findByEmail(email);
        assertThat(userByEmail, is(user));
        assertThat(userByEmail.getEmail(), is(email));
    }

    @Test
    public void findOneByAccountStatusVerifyKey_keyDoesNotExist() {
        User user = userDao.findOneByAccountStatusVerifyKey("afsaf");
        assertThat(user, is(nullValue()));
    }

    @Test
    public void findOneByAccountStatusVerifyKey_keyDoesExist() {

        User userOne = a(user()
                .withAccountStatus(
                        new AccountStatus(false, "someKey")
                )
        );
        userDao.save(userOne);
        User user = userDao.findOneByAccountStatusVerifyKey("someKey");
        assertThat(user, is(userOne));
    }


}
