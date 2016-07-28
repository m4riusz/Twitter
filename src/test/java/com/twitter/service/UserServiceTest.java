package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.exception.UserAlreadyExistsException;
import com.twitter.exception.UserFollowException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.exception.UserUnfollowException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.UserBuilder.user;
import static java.util.Collections.emptyList;
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

    @Test
    public void unfollow_userFollowsOtherUser() {
        User user = a(user().withId(2L));
        User userToUnfollow = a(user().withId(1L).withFollowers(aListWith(user)));
        when(userDao.findOne(anyLong())).thenReturn(userToUnfollow);
        Result<Boolean> unfollowResult = userService.unfollow(user, userToUnfollow.getId());
        assertThat(userToUnfollow.getFollowers(), not(hasItem(user)));
        assertThat(unfollowResult.isSuccess(), is(true));
        assertThat(unfollowResult.getValue(), is(true));
    }

    @Test(expected = UserUnfollowException.class)
    public void unfollow_userUnfollowsHimself() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.unfollow(user, user.getId());
    }

    @Test(expected = UserUnfollowException.class)
    public void unfollow_userNotFollowed() {
        User user = a(user().withId(1L));
        User userToUnfollow = a(user().withId(2L));
        when(userDao.findOne(anyLong())).thenReturn(userToUnfollow);
        userService.unfollow(user, userToUnfollow.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void unfollow_userToUnfollowDoesNotExists() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.unfollow(user, 1L);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserById_userDoesNotExist() {
        long userId = 1L;
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.deleteUserById(userId);
    }

    @Test
    public void deleteUserById_userExists() {
        long userId = 1L;
        when(userDao.exists(anyLong())).thenReturn(true);
        Result<Boolean> removeUser = userService.deleteUserById(userId);
        assertThat(removeUser.isSuccess(), is(true));
    }

    @Test
    public void getAllUsersCount_noUsers() {
        when(userDao.count()).thenReturn(0L);
        Result<Long> allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount.isSuccess(), is(true));
        assertThat(allUsersCount.getValue(), is(equalTo(0L)));
    }

    @Test
    public void getAllUsersCount_someUsers() {
        when(userDao.count()).thenReturn(5L);
        Result<Long> allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount.isSuccess(), is(true));
        assertThat(allUsersCount.getValue(), is(equalTo(5L)));
    }

    @Test
    public void getAllUsers_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(userOne, userTwo)));
        Result<List<User>> allUsers = userService.getAllUsers(new PageRequest(0, 10));
        assertThat(allUsers.isSuccess(), is(true));
        assertThat(allUsers.getValue(), hasItems(userOne, userTwo));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowersCountById_userDoesNotExists() {
        when(userDao.exists(1L)).thenReturn(false);
        userService.getUserFollowersCountById(1L);
    }

    @Test
    public void getUserFollowersCountById_userExistsNoFollowers() {
        long userId = 1L;
        when(userDao.exists(userId)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(userId)).thenReturn(0L);
        Result<Long> userFollowersCountById = userService.getUserFollowersCountById(userId);
        assertThat(userFollowersCountById.isSuccess(), is(true));
        assertThat(userFollowersCountById.getValue(), is(equalTo(0L)));
    }

    @Test
    public void getUserFollowersCountById_userExistsSomeFollowers() {
        long userId = 1L;
        when(userDao.exists(userId)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(anyLong())).thenReturn(2L);
        Result<Long> userFollowersById = userService.getUserFollowersCountById(userId);
        assertThat(userFollowersById.isSuccess(), is(true));
        assertThat(userFollowersById.getValue(), is(equalTo(2L)));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowersById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserFollowersById(1L, new PageRequest(0, 10));
    }

    @Test
    public void getUserFollowersById_noFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowersByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<User>> userFollowersById = userService.getUserFollowersById(1L, new PageRequest(0, 10));
        assertThat(userFollowersById.isSuccess(), is(true));
        assertThat(userFollowersById.getValue(), is(emptyList()));
    }

    @Test
    public void getUserFollowersById_someUsers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.findFollowersByUserId(anyLong(), any(Pageable.class))).thenReturn(aListWith(userOne, userTwo));
        Result<List<User>> userFollowersById = userService.getUserFollowersById(1L, new PageRequest(0, 10));
        assertThat(userFollowersById.isSuccess(), is(true));
        assertThat(userFollowersById.getValue(), hasItems(userOne, userTwo));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowingCountById_userDoesNotExists() {
        when(userDao.exists(1L)).thenReturn(false);
        userService.getUserFollowingCountById(1L);
    }

    @Test
    public void getUserFollowingCountById_userExistsNoFollowers() {
        long userId = 1L;
        when(userDao.exists(userId)).thenReturn(true);
        when(userDao.findFollowingCountByUserId(userId)).thenReturn(0L);
        Result<Long> userFollowingCountById = userService.getUserFollowingCountById(userId);
        assertThat(userFollowingCountById.isSuccess(), is(true));
        assertThat(userFollowingCountById.getValue(), is(equalTo(0L)));
    }

    @Test
    public void getUserFollowingCountById_userExistsSomeFollowers() {
        long userId = 1L;
        when(userDao.exists(userId)).thenReturn(true);
        when(userDao.findFollowingCountByUserId(anyLong())).thenReturn(2L);
        Result<Long> userFollowersById = userService.getUserFollowingCountById(userId);
        assertThat(userFollowersById.isSuccess(), is(true));
        assertThat(userFollowersById.getValue(), is(equalTo(2L)));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowingsById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserFollowingsById(1L, new PageRequest(0, 10));
    }

    @Test
    public void getUserFollowingsById_noFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<User>> userFollowingsById = userService.getUserFollowingsById(1L, new PageRequest(0, 10));
        assertThat(userFollowingsById.isSuccess(), is(true));
        assertThat(userFollowingsById.getValue(), is(emptyList()));
    }

    @Test
    public void getUserFollowingsById_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingByUserId(anyLong(), any(Pageable.class))).thenReturn(aListWith(userOne, userTwo));
        Result<List<User>> userFollowingsById = userService.getUserFollowingsById(1L, new PageRequest(0, 10));
        assertThat(userFollowingsById.isSuccess(), is(true));
        assertThat(userFollowingsById.getValue(), hasItems(userOne, userTwo));
    }

}
