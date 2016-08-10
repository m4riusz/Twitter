package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.UserDao;
import com.twitter.model.AccountStatus;
import com.twitter.model.Result;
import com.twitter.model.Role;
import com.twitter.model.User;
import com.twitter.util.MessageUtil;
import com.twitter.util.TestUtil;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.ResultIsFailureMatcher.hasFailed;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultMessageMatcher.hasMessageOf;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
import static com.twitter.matchers.UserFollowerMatcher.hasFollowers;
import static com.twitter.matchers.UserIsBanned.isBanned;
import static com.twitter.matchers.UserIsEnabled.isEnabled;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by mariusz on 14.07.16.
 */

@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
public class  UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private JavaMailSender javaMailSender;
    private UserService userService;
    private Authentication authentication;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userDao, javaMailSender);
        authentication = mock(Authentication.class);
        mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void getUserByIdTest_userExists() {
        User user = a(user());
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        Result<User> userResult = userService.getUserById(TestUtil.ID_ONE);
        assertThat(userResult, hasFinishedSuccessfully());
        assertThat(userResult, hasValueOf(user));
        assertThat(userResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserByIdTest_userDoesNotExists() {
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(null);
        Result<User> userResult = userService.getUserById(TestUtil.ID_ONE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
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
        assertThat(createResult, hasValueOf(true));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void create_usernameAlreadyExist() {
        when(userDao.findByUsername(anyString())).thenReturn(a(user()));
        Result<Boolean> userResult = userService.create(a(user()));
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_ALREADY_EXISTS_USERNAME_ERROR_MSG));
    }

    @Test
    public void create_emailAlreadyExist() {
        User user = a(user());
        when(userDao.findByEmail(anyString())).thenReturn(user);
        Result<Boolean> userResult = userService.create(user);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG));
    }

    @Test
    public void follow_userFollowsOtherUser() {
        User userOne = a(user().withId(TestUtil.ID_ONE));
        User userTwo = a(user().withId(TestUtil.ID_TWO));
        when(authentication.getName()).thenReturn(userTwo.getUsername());
        when(userDao.findByUsername(userTwo.getUsername())).thenReturn(userTwo);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(userOne);
        Result<Boolean> followResult = userService.follow(userOne.getId());
        assertThat(userOne, hasFollowers(userTwo));
        assertThat(followResult, hasFinishedSuccessfully());
        assertThat(followResult, hasValueOf(true));
        assertThat(followResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void follow_userFollowHimself() {
        User user = a(user().withId(TestUtil.ID_ONE));
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        Result<Boolean> userResult = userService.follow(user.getId());
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.FOLLOW_YOURSELF_ERROR_MSG));
    }

    @Test
    public void follow_userAlreadyFollowed() {
        User user = a(user().withId(TestUtil.ID_ONE));
        User userToFollow = a(user().withId(TestUtil.ID_TWO).withFollowers(aListWith(user)));
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToFollow);
        Result<Boolean> userResult = userService.follow(userToFollow.getId());
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.FOLLOW_ALREADY_FOLLOWED_ERROR_MSG));
    }

    @Test
    public void follow_userToFollowDoesNotExists() {
        User user = a(user());
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(anyLong())).thenReturn(null);
        Result<Boolean> userResult = userService.follow(TestUtil.ID_ONE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void unfollow_userFollowsOtherUser() {
        User user = a(user().withId(TestUtil.ID_ONE));
        User userToUnfollow = a(user().withId(TestUtil.ID_TWO).withFollowers(aListWith(user)));
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToUnfollow);
        Result<Boolean> unfollowResult = userService.unfollow(userToUnfollow.getId());
        assertThat(userToUnfollow, not(hasFollowers(user)));
        assertThat(unfollowResult, hasFinishedSuccessfully());
        assertThat(unfollowResult, hasValueOf(true));
        assertThat(unfollowResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void unfollow_userUnfollowsHimself() {
        User user = a(user());
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(anyLong())).thenReturn(user);
        Result<Boolean> userResult = userService.unfollow(user.getId());
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.UNFOLLOW_YOURSELF_ERROR_MSG));
    }

    @Test
    public void unfollow_userNotFollowed() {
        User user = a(user().withId(TestUtil.ID_ONE));
        User userToUnfollow = a(user().withId(TestUtil.ID_TWO));
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(anyLong())).thenReturn(userToUnfollow);
        Result<Boolean> userResult = userService.unfollow(userToUnfollow.getId());
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.UNFOLLOW_UNFOLLOWED_ERROR_MSG));
    }

    @Test
    public void unfollow_userToUnfollowDoesNotExists() {
        User user = a(user());
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userDao.findByUsername(user.getUsername())).thenReturn(user);
        when(userDao.findOne(anyLong())).thenReturn(null);
        Result<Boolean> userResult = userService.unfollow(TestUtil.ID_ONE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void deleteUserById_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(false);
        Result<Boolean> userResult = userService.deleteUserById(TestUtil.ID_ONE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void deleteUserById_userExists() {
        when(userDao.exists(anyLong())).thenReturn(true);
        Result<Boolean> removeUser = userService.deleteUserById(TestUtil.ID_ONE);
        assertThat(removeUser, hasFinishedSuccessfully());
        assertThat(removeUser, hasValueOf(true));
        assertThat(removeUser, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getAllUsersCount_noUsers() {
        when(userDao.count()).thenReturn(0L);
        Result<Long> allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount, hasFinishedSuccessfully());
        assertThat(allUsersCount, hasValueOf(0L));
        assertThat(allUsersCount, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getAllUsersCount_someUsers() {
        when(userDao.count()).thenReturn(5L);
        Result<Long> allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount, hasFinishedSuccessfully());
        assertThat(allUsersCount, hasValueOf(5L));
        assertThat(allUsersCount, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getAllUsers_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(userOne, userTwo)));
        Result<List<User>> allUsers = userService.getAllUsers(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allUsers, hasFinishedSuccessfully());
        assertThat(allUsers, hasValueOf(aListWith(userOne, userTwo)));
        assertThat(allUsers, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserFollowersCountById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        Result<Long> userResult = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getUserFollowersCountById_userExistsNoFollowers() {
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(TestUtil.ID_ONE)).thenReturn(0L);
        Result<Long> userFollowersCountById = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userFollowersCountById, hasFinishedSuccessfully());
        assertThat(userFollowersCountById, hasValueOf(0L));
        assertThat(userFollowersCountById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserFollowersCountById_userExistsSomeFollowers() {
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(TestUtil.ID_ONE)).thenReturn(2L);
        Result<Long> userFollowersById = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(2L));
        assertThat(userFollowersById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserFollowersById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        Result<List<User>> userResult = userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getUserFollowersById_noFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowersByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<User>> userFollowersById = userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(emptyList()));
        assertThat(userFollowersById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
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
        assertThat(userFollowersById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserFollowingCountById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        Result<Long> userResult = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getUserFollowingCountById_userExistsNoFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingCountByUserId(anyLong())).thenReturn(0L);
        Result<Long> userFollowingCountById = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userFollowingCountById, hasFinishedSuccessfully());
        assertThat(userFollowingCountById, hasValueOf(0L));
        assertThat(userFollowingCountById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserFollowingCountById_userExistsSomeFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingCountByUserId(anyLong())).thenReturn(2L);
        Result<Long> userFollowersById = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userFollowersById, hasFinishedSuccessfully());
        assertThat(userFollowersById, hasValueOf(2L));
        assertThat(userFollowersById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getUserFollowingsById_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        Result<List<User>> userResult = userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getUserFollowingsById_noFollowers() {
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowingByUserId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<User>> userFollowingsById = userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowingsById, hasFinishedSuccessfully());
        assertThat(userFollowingsById, hasValueOf(emptyList()));
        assertThat(userFollowingsById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
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
        assertThat(userFollowingsById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void activateAccount_verifyKeyNotFound() {
        when(userDao.findOneByAccountStatusVerifyKey(anyString())).thenReturn(null);
        Result<Boolean> activateResult = userService.activateAccount("someKey");
        assertThat(activateResult, hasFailed());
        assertThat(activateResult, hasMessageOf(MessageUtil.INVALID_VERIFY_KEY));
    }

    @Test
    public void activateAccount_accountAlreadyActivated() {
        User user = a(user().withAccountStatus(new AccountStatus(true, "someKey")));
        when(userDao.findOneByAccountStatusVerifyKey(anyString())).thenReturn(user);
        Result<Boolean> activateResult = userService.activateAccount("someKey");
        assertThat(activateResult, hasFailed());
        assertThat(activateResult, hasMessageOf(MessageUtil.ACCOUNT_HAS_BEEN_ALREADY_ENABLED));
    }

    @Test
    public void activateAccount_accountNotActivated() {
        User user = a(user().withAccountStatus(new AccountStatus(false, "someKey")));
        when(userDao.findOneByAccountStatusVerifyKey(anyString())).thenReturn(user);
        Result<Boolean> activateResult = userService.activateAccount("someKey");
        assertThat(user, isEnabled(true));
        assertThat(activateResult, hasFinishedSuccessfully());
        assertThat(activateResult, hasValueOf(true));
        assertThat(activateResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void banUser_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        Result<Boolean> userResult = userService.banUser(1L, DateTime.now().toDate());
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void banUser_invalidDate() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        Result<Boolean> userResult = userService.banUser(user.getId(), null);
        assertThat(user, isBanned(false));
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.REPORT_DATE_NOT_SET_ERROR_MSG));
    }

    @Test
    public void banUser_dateBeforeNow() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        Result<Boolean> userResult = userService.banUser(user.getId(), TestUtil.DATE_2003);
        assertThat(user, isBanned(false));
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG));
    }

    @Test
    public void banUser_userExist() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        Date futureDate = DateTime.now().plusDays(10).toDate();
        Result<Boolean> userResult = userService.banUser(user.getId(), futureDate);
        assertThat(user, isBanned(true));
        assertThat(userResult, hasFinishedSuccessfully());
        assertThat(userResult, hasValueOf(true));
        assertThat(userResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void unbanUser_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        Result<Boolean> userResult = userService.unbanUser(1L);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void unbanUser_userExist() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        Result<Boolean> userResult = userService.unbanUser(user.getId());
        assertThat(user, isBanned(false));
        assertThat(userResult, hasFinishedSuccessfully());
        assertThat(userResult, hasValueOf(true));
        assertThat(userResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void changeUserRole_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        Result<Boolean> userResult = userService.changeUserRole(1L, Role.MOD);
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void changeUserRole_userExists() {
        User user = a(user().withRole(Role.USER));
        when(userDao.findOne(anyLong())).thenReturn(user);
        Result<Boolean> userResult = userService.changeUserRole(user.getId(), Role.MOD);
        assertThat(user.getRole(), is(Role.MOD));
        assertThat(userResult, hasFinishedSuccessfully());
        assertThat(userResult, hasValueOf(true));
        assertThat(userResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void changeUserPasswordById_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        Result<Boolean> userResult = userService.changeUserPasswordById(1L, "NewPass");
        assertThat(userResult, hasFailed());
        assertThat(userResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void changeUserPasswordById_userExists() {
        User user = a(user());
        when(userDao.findOne(anyLong())).thenReturn(user);
        Result<Boolean> userResult = userService.changeUserPasswordById(user.getId(), "newPassword");
        assertThat(user.getPassword(), is("newPassword"));
        assertThat(userResult, hasFinishedSuccessfully());
        assertThat(userResult, hasValueOf(true));
        assertThat(userResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }
}


