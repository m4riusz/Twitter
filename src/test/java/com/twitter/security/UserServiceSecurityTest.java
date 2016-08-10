package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.model.Role;
import com.twitter.service.UserService;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 07.08.16.
 */

@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class UserServiceSecurityTest {

    public static final String ADMIN = "ADMIN";
    public static final String MODERATOR = "MODERATOR";
    public static final String USER = "USER";

    @MockBean
    private UserService userService;

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserByIdTest_anonymousAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void follow_anonymousAccessDenied() {
        userService.follow(a(user()), TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void unfollow_anonymousAccessDenied() {
        userService.unfollow(a(user()), TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void banUser_anonymousAccessDenied() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithMockUser(authorities = USER)
    @Test(expected = AccessDeniedException.class)
    public void banUser_userAccessDenied() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithMockUser(authorities = MODERATOR)
    @Test
    public void banUser_moderatorAccess() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithMockUser(authorities = ADMIN)
    @Test
    public void banUser_adminAccess() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void changeUserRole_anonymousAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithMockUser(authorities = USER)
    @Test(expected = AccessDeniedException.class)
    public void changeUserRole_userAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithMockUser(authorities = MODERATOR)
    @Test(expected = AccessDeniedException.class)
    public void changeUserRole_moderatorAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithMockUser(authorities = ADMIN)
    @Test
    public void changeUserRole_adminAccess() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void deleteUserById_anonymousAccessDenied() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithMockUser(authorities = USER)
    @Test(expected = AccessDeniedException.class)
    public void deleteUserById_userAccessDenied() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithMockUser(authorities = MODERATOR)
    @Test
    public void deleteUserById_moderatorAccess() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithMockUser(authorities = ADMIN)
    @Test
    public void deleteUserById_adminAccess() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllUsersCount_anonymousAccessDenied() {
        userService.getAllUsersCount();
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllUsers_anonymousAccessDenied() {
        userService.getAllUsers(TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowersCountById_anonymousAccessDenied() {
        userService.getUserFollowersCountById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowersById_anonymousAccessDenied() {
        userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowingCountById_anonymousAccessDenied() {
        userService.getUserFollowingCountById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowingsById_anonymousAccessDenied() {
        userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test
    public void activateAccount_anonymousAccessDenied() {
        userService.activateAccount("secret_activation_key");
    }

}