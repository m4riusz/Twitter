package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.exception.UserAlreadyExistsException;
import com.twitter.exception.UserFollowException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.Result;
import com.twitter.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.UserBuilder.user;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 14.07.16.
 */

@SpringBootTest
@RunWith(value = MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService = new UserServiceImpl(userDao);

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUserByIdTest_userExists() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        User userById = userService.getUserById(1L).getValue();

        assertThat(userById, is(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdTest_userDoesNotExists() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.getUserById(1L);
    }

    @Test
    public void loadUserByUsername_userExists() {
        String username = "Mariusz";
        User user = a(user().withUsername(username));
        when(userDao.findByUsername(anyString())).thenReturn(user);
        User userByUsername = userService.loadUserByUsername(username);
        assertThat(userByUsername, is(user));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_userDoesNotExists() {
        when(userDao.findByUsername(anyString())).thenReturn(null);
        User user = userService.loadUserByUsername("Username");
        assertThat(user, is(nullValue()));
    }

    @Test
    public void create_userCreateTest() {
        User user = a(user());
        when(userDao.findByUsername(anyString())).thenReturn(null);
        when(userDao.save(any(User.class))).thenReturn(user);
        Result<Boolean> createResult = userService.create(user);
        assertTrue(createResult.isSuccess());
        assertThat(createResult.getValue(), is(Boolean.TRUE));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void create_userAlreadyExists() {
        when(userDao.findByUsername(anyString())).thenReturn(a(user()));
        userService.create(a(user()));
    }

    @Test
    public void follow_userFollowsOtherUser() {
        User userToFollow = a(user().withId(1L));
        User user = a(user().withId(2L));
        when(userDao.findOne(anyLong())).thenReturn(userToFollow);
        Result<Boolean> followResult = userService.follow(user, userToFollow.getId());
        assertThat(userToFollow.getFollowers(), hasItem(user));
        assertThat(followResult.isSuccess(), is(true));
        assertThat(followResult.getValue(), is(true));
    }

    @Test(expected = UserFollowException.class)
    public void follow_userFollowHimself() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.follow(user, user.getId());
    }

    @Test(expected = UserFollowException.class)
    public void follow_userAlreadyFollowed() {
        User user = a(user().withId(1L));
        User userToFollow = a(user().withId(2L).withFollowers(aListWith(user)));
        when(userDao.findOne(anyLong())).thenReturn(userToFollow);
        userService.follow(user, userToFollow.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void follow_userToFollowDoesNotExists() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.follow(user, 1L);
    }

    
}
