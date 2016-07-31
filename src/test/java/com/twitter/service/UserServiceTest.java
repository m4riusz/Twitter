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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
import static com.twitter.matchers.UserFollowerMatcher.hasFollowers;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 14.07.16.
 */

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class  UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JavaMailSender javaMailSender;

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userDao, javaMailSender);
    }

    @Test
    public void getUserByIdTest_userExists() {
        User user = a(user());
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        User userById = userService.getUserById(TestUtil.ID_ONE).getValue();
        assertThat(userById, is(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdTest_userDoesNotExists() {
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(null);
        userService.getUserById(TestUtil.ID_ONE);
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
        assertThat(createResult, hasFinishedSuccessfully());
        assertThat(createResult, hasValueOf(Boolean.TRUE));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void create_userAlreadyExists() {
        when(userDao.findByUsername(anyString())).thenReturn(a(user()));
        userService.create(a(user()));
    }

    @Test
    public void follow_userFollowsOtherUser() {
        User userOne = a(user().withId(TestUtil.ID_ONE));
        User userTwo = a(user().withId(TestUtil.ID_TWO));
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(userOne);
        Result<Boolean> followResult = userService.follow(userTwo, userOne.getId());
        assertThat(userOne, hasFollowers(userTwo));
        assertThat(followResult, hasFinishedSuccessfully());
        assertThat(followResult, hasValueOf(Boolean.TRUE));
    }

    @Test(expected = UserFollowException.class)
    public void follow_userFollowHimself() {
        User user = a(user().withId(TestUtil.ID_ONE));
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        userService.follow(user, user.getId());
    }

    @Test(expected = UserFollowException.class)
    public void follow_userAlreadyFollowed() {
        User user = a(user().withId(TestUtil.ID_ONE));
        User userToFollow = a(user().withId(TestUtil.ID_TWO).withFollowers(aListWith(user)));
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToFollow);
        userService.follow(user, userToFollow.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void follow_userToFollowDoesNotExists() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.follow(user, TestUtil.ID_ONE);
    }

    @Test
    public void unfollow_userFollowsOtherUser() {
        User user = a(user().withId(TestUtil.ID_ONE));
        User userToUnfollow = a(user().withId(TestUtil.ID_TWO).withFollowers(aListWith(user)));
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToUnfollow);
        Result<Boolean> unfollowResult = userService.unfollow(user, userToUnfollow.getId());
        assertThat(userToUnfollow, not(hasFollowers(user)));
        assertThat(unfollowResult, hasFinishedSuccessfully());
        assertThat(unfollowResult, hasValueOf(Boolean.TRUE));
    }

    @Test(expected = UserUnfollowException.class)
    public void unfollow_userUnfollowsHimself() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.unfollow(user, user.getId());
    }

    @Test(expected = UserUnfollowException.class)
    public void unfollow_userNotFollowed() {
        User user = a(user().withId(TestUtil.ID_ONE));
        User userToUnfollow = a(user().withId(TestUtil.ID_TWO));
        when(userDao.findOne(anyLong())).thenReturn(userToUnfollow);
        userService.unfollow(user, userToUnfollow.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void unfollow_userToUnfollowDoesNotExists() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.unfollow(user, TestUtil.ID_ONE);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserById_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @Test
    public void deleteUserById_userExists() {
        when(userDao.exists(anyLong())).thenReturn(true);
        Result<Boolean> removeUser = userService.deleteUserById(TestUtil.ID_ONE);
        assertThat(removeUser, hasFinishedSuccessfully());
        assertThat(removeUser, hasValueOf(Boolean.TRUE));
    }

    @Test
    public void getAllUsersCount_noUsers() {
        when(userDao.count()).thenReturn(0L);
        Result<Long> allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount, hasFinishedSuccessfully());
        assertThat(allUsersCount, hasValueOf(0L));
    }

    @Test
    public void getAllUsersCount_someUsers() {
        when(userDao.count()).thenReturn(5L);
        Result<Long> allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount, hasFinishedSuccessfully());
        assertThat(allUsersCount, hasValueOf(5L));
    }

    @Test
    public void getAllUsers_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(userOne, userTwo)));
        Result<List<User>> allUsers = userService.getAllUsers(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allUsers, hasFinishedSuccessfully());
        assertThat(allUsers, hasValueOf(aListWith(userOne, userTwo)));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowersCountById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserFollowersCountById(TestUtil.ID_ONE);
    }

    @Test
    public void getUserFollowersCountById_userExistsNoFollowers() {
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(TestUtil.ID_ONE)).thenReturn(0L);
        Result<Long> userFollowersCountById = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userFollowersCountById, hasFinishedSuccessfully());
        assertThat(userFollowersCountById, hasValueOf(0L));
    }

    @Test
    public void getUserFollowersCountById_userExistsSomeFollowers() {

        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(TestUtil.ID_ONE)).thenReturn(2L);
        Result<Long> userFollowersById = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(2L));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowersById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getUserFollowersById_noFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowersByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<User>> userFollowersById = userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(emptyList()));
    }

    @Test
    public void getUserFollowersById_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowersByUserId(anyLong(), any(Pageable.class))).thenReturn(aListWith(userOne, userTwo));
        Result<List<User>> userFollowersById = userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(aListWith(userOne, userTwo)));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowingCountById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserFollowingCountById(TestUtil.ID_ONE);
    }

    @Test
    public void getUserFollowingCountById_userExistsNoFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingCountByUserId(anyLong())).thenReturn(0L);
        Result<Long> userFollowingCountById = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userFollowingCountById, hasFinishedSuccessfully());
        assertThat(userFollowingCountById, hasValueOf(0L));
    }

    @Test
    public void getUserFollowingCountById_userExistsSomeFollowers() {

        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingCountByUserId(anyLong())).thenReturn(2L);
        Result<Long> userFollowersById = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(2L));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFollowingsById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getUserFollowingsById_noFollowers() {
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowingByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<User>> userFollowingsById = userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowingsById, hasFinishedSuccessfully());
        assertThat(userFollowingsById, hasValueOf(emptyList()));
    }

    @Test
    public void getUserFollowingsById_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowingByUserId(anyLong(), any(Pageable.class))).thenReturn(aListWith(userOne, userTwo));
        Result<List<User>> userFollowingsById = userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowingsById, hasFinishedSuccessfully());
        assertThat(userFollowingsById, hasValueOf(aListWith(userOne, userTwo)));
    }

}
