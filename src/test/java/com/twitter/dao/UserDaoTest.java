/*
package com.twitter.dao;

import com.twitter.Builder;
import com.twitter.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.twitter.dao.UserBuilder.user;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

*/
/**
 * Created by mariusz on 12.07.16.
 *//*

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

    @Test(expected = Exception.class)
    public void saveUserThatAlreadyExists() {
        User user1 = a(user().withUsername("Username"));
        User user2 = a(user().withUsername("Username"));
        userDao.save(aUserListWith(user1, user2));
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
        User expectedUser = a(user().withId(1).withUsername("Mariusz"));
        User otherUser = a(user().withId(2).withUsername("Marcin"));
        userDao.save(aUserListWith(expectedUser, otherUser));
        User userFromDatabase = userDao.findOne(1L);
        assertThat(userFromDatabase, is(expectedUser));
    }

    @Test
    public void fetchUserThatDoesNotExist() {
        User user1 = a(user().withId(1).withUsername("Mariusz"));
        User user2 = a(user().withId(2).withUsername("Marcin"));
        userDao.save(aUserListWith(user1, user2));
        User userFromDatabase = userDao.findOne(3L);
        assertThat(userFromDatabase, is(nullValue()));
    }

    @Test
    public void fetchUserPagingTest() {
        User user1 = a(user().withId(1).withUsername("Mariusz"));
        User user2 = a(user().withId(2).withUsername("Marcin"));
        User user3 = a(user().withId(3).withUsername("Dominik"));
        User user4 = a(user().withId(4).withUsername("Adam"));
        userDao.save(aUserListWith(user1, user2, user3, user4));

        List<User> usersOnFirstPage = userDao.findAll(new PageRequest(0, 2)).getContent();
        List<User> usersOnSecondPage = userDao.findAll(new PageRequest(1, 2)).getContent();

        assertThat(usersOnFirstPage, hasItems(user1, user2));
        assertThat(usersOnSecondPage, hasItems(user3, user4));
    }

    @Test
    public void updateUserTest() {
        User user = a(user().withId(1).withUsername("Mariusz"));
        userDao.saveAndFlush(user);

        User userFromDatabase = userDao.findOne(1L);
        userFromDatabase.setUsername("Marcin");
        userDao.flush();

        User userFromDatabase2 = userDao.findOne(1L);
        assertThat(userFromDatabase2.getUsername(), is("Marcin"));
        assertThat(userDao.findAll().size(), is(1));
    }

    @Test
    public void deleteUserTest() {
        User user1 = a(user().withId(1).withUsername("u1"));
        User user2 = a(user().withId(2).withUsername("u2"));
        User user3 = a(user().withId(3).withUsername("u3"));
        userDao.save(aUserListWith(user1, user2, user3));
        userDao.delete(1L);
        List<User> allUsers = userDao.findAll();
        assertThat(allUsers, not(hasItem(user1)));
        assertThat(allUsers, hasItem(user2));
        assertThat(allUsers, hasItem(user3));
    }

    @Test
    public void findFollowersByUserId() {
        User user1 = a(user().withId(1).withUsername("u1"));
        User user2 = a(user().withId(2).withUsername("u2"));
        User user3 = a(user().withId(3).withUsername("u3"));

        userDao.save(aUserListWith(user1, user2, user3));

        user1.getFollowers().add(user2);
        user1.getFollowers().add(user3);
        userDao.save(user1);
        List<User> followersFromUser1 = userDao.findFollowersByUserId(1L);

        assertThat(followersFromUser1, hasItems(user2, user3));
    }

    @Test
    public void findFollowersByUserId_pagingTest() {
        User user1 = a(user().withId(1).withUsername("u1"));
        User user2 = a(user().withId(2).withUsername("u2"));
        User user3 = a(user().withId(3).withUsername("u3"));
        User user4 = a(user().withId(4).withUsername("u4"));

        userDao.save(aUserListWith(user1, user2, user3, user4));

        user1.getFollowers().add(user2);
        user1.getFollowers().add(user3);
        user1.getFollowers().add(user4);
        userDao.save(user1);
        List<User> user1PageOne = userDao.findFollowersByUserId(1L, new PageRequest(0, 2));
        List<User> user1PageTwo = userDao.findFollowersByUserId(1L, new PageRequest(1, 2));

        assertThat(user1PageOne, hasItems(user2, user3));
        assertThat(user1PageTwo, hasItem(user4));
    }

    @Test
    public void findFollowingByUserId() {
        User user1 = a(user().withId(1).withUsername("u1"));
        User user2 = a(user().withId(2).withUsername("u2"));
        User user3 = a(user().withId(3).withUsername("u3"));

        userDao.save(aUserListWith(user1, user2, user3));

        user1.getFollowers().add(user2);
        user3.getFollowers().add(user2);
        userDao.save(aUserListWith(user1, user3));
        List<User> user2FollowingList = userDao.findFollowingByUserId(2L);

        assertThat(user2FollowingList, hasItems(user1, user3));
    }

    @Test
    public void findFollowingByUserId_pagingTest() {
        User user1 = a(user().withId(1).withUsername("u1"));
        User user2 = a(user().withId(2).withUsername("u2"));
        User user3 = a(user().withId(3).withUsername("u3"));
        User user4 = a(user().withId(4).withUsername("u4"));

        userDao.save(aUserListWith(user1, user2, user3, user4));

        user1.getFollowers().add(user2);
        user3.getFollowers().add(user2);
        user4.getFollowers().add(user2);
        userDao.save(aUserListWith(user1, user3, user4));
        List<User> user2PageOne = userDao.findFollowingByUserId(2L, new PageRequest(0, 2));
        List<User> user2PageTwo = userDao.findFollowingByUserId(2L, new PageRequest(1, 2));

        assertThat(user2PageOne, hasItems(user1, user3));
        assertThat(user2PageTwo, hasItem(user4));
    }


    private List<User> aUserListWith(User... users) {
        return Arrays.asList(users);
    }

    private <T> T a(Builder<T> builder) {
        return builder.build();
    }


}
*/
