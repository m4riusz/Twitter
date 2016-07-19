package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static com.twitter.Util.a;
import static com.twitter.builders.UserBuilder.user;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 14.07.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Autowired
    @InjectMocks
    private UserService userService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUserByIdTest_userExists() {
        long existingUserId = 1L;
        User user = a(user().withId(existingUserId));
        when(userDao.findOne(existingUserId)).thenReturn(user);
        User userById = userService.getUserById(existingUserId);

        assertThat(userById, is(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdTest_userDoesNotExists() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.getUserById(1L);
    }

    @Test
    public void getUserByUsername_userExists() {
        String username = "Mariusz";
        User user = a(user().withUsername(username));
        when(userDao.findByUsername(username)).thenReturn(user);
        User userById = userService.loadUserByUsername(username);
        assertThat(userById, is(user));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void getUserByUsername_userDoesNotExists() {
        when(userDao.findByUsername(anyString())).thenReturn(null);
        userService.loadUserByUsername("Username");
    }

}
