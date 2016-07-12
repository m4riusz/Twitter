package com.twitter.dao;

import com.twitter.Builder;
import com.twitter.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.twitter.dao.UserBuilder.user;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 12.07.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void saveOneUserTest() {
        User user = a(user());
        userDao.save(aUserListWith(user));
        List<User> allUsers = userDao.findAll();
        assertThat(allUsers.size(), is(1));
    }

    @Test
    public void saveMultipleUsersTest() {
        User user1 = a(user().withUsername("u1"));
        User user2 = a(user().withUsername("u2"));
        User user3 = a(user().withUsername("u3"));
        userDao.save(aUserListWith(user1, user2, user3));
        List<User> allUsers = userDao.findAll();
        assertThat(allUsers.size(), is(3));
    }

    @Test
    public void fetchOneUserTest() {
        long count = userDao.count();
        User user = a(user().withId(count + 1).withUsername("Mariusz"));
        userDao.save(aUserListWith(user));
        User userFromDatabase = userDao.findOne(count + 1);
        assertThat(user, is(userFromDatabase));
    }

    private List<User> aUserListWith(User... users) {
        return Arrays.asList(users);
    }

    private <T> T a(Builder<T> builder) {
        return builder.build();
    }


}
