package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.model.Role;
import com.twitter.service.UserService;
import com.twitter.util.TestUtil;
import com.twitter.util.WithCustomMockUser;
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

    @MockBean
    private UserService userService;

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserByIdTest_anonymousAccessDenied() {
        userService.getUserById(TestUtil.ID_ONE);
    }

    @WithMockUser
    @Test
    public void getUserByIdTest_userAuthenticatedAccess() {
        userService.getUserById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void follow_anonymousAccessDenied() {
        userService.follow(TestUtil.ID_ONE);
    }

    @WithMockUser
    @Test
    public void follow_userAuthenticatedAccess() {
        userService.follow(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void unfollow_anonymousAccessDenied() {
        userService.unfollow(TestUtil.ID_ONE);
    }

    @WithMockUser
    @Test
    public void unfollow_userAuthenticatedAccess() {
        userService.unfollow(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void banUser_anonymousAccessDenied() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithMockUser(authorities = TestUtil.USER)
    @Test(expected = AccessDeniedException.class)
    public void banUser_userAccessDenied() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithMockUser(authorities = TestUtil.MODERATOR)
    @Test
    public void banUser_moderatorAccess() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithMockUser(authorities = TestUtil.ADMIN)
    @Test
    public void banUser_adminAccess() {
        userService.banUser(TestUtil.ID_ONE, new Date());
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void changeUserRole_anonymousAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithMockUser(authorities = TestUtil.USER)
    @Test(expected = AccessDeniedException.class)
    public void changeUserRole_userAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithMockUser(authorities = TestUtil.MODERATOR)
    @Test(expected = AccessDeniedException.class)
    public void changeUserRole_moderatorAccessDenied() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithMockUser(authorities = TestUtil.ADMIN)
    @Test
    public void changeUserRole_adminAccess() {
        userService.changeUserRole(TestUtil.ID_ONE, Role.ADMIN);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void deleteUserById_anonymousAccessDenied() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithMockUser(authorities = TestUtil.USER)
    @Test(expected = AccessDeniedException.class)
    public void deleteUserById_userAccessDenied() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithMockUser(authorities = TestUtil.MODERATOR)
    @Test
    public void deleteUserById_moderatorAccess() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithMockUser(authorities = TestUtil.ADMIN)
    @Test
    public void deleteUserById_adminAccess() {
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllUsersCount_anonymousAccessDenied() {
        userService.getAllUsersCount();
    }

    @WithMockUser
    @Test
    public void getAllUsersCount_userAuthenticatedAccess() {
        userService.getAllUsersCount();
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllUsers_anonymousAccessDenied() {
        userService.getAllUsers(TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithMockUser
    @Test
    public void getAllUsers_userAuthenticatedAccess() {
        userService.getAllUsers(TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowersCountById_anonymousAccessDenied() {
        userService.getUserFollowersCountById(TestUtil.ID_ONE);
    }

    @WithMockUser
    @Test
    public void getUserFollowersCountById_userAuthenticatedAccess() {
        userService.getUserFollowersCountById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowersById_anonymousAccessDenied() {
        userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithMockUser
    @Test
    public void getUserFollowersById_userAuthenticatedAccess() {
        userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getCurrentLoggedUser_anonymousAccessDenied() {
        userService.getCurrentLoggedUser();
    }

    @WithMockUser
    @Test
    public void getCurrentLoggedUser_userAuthenticatedAccess() {
        userService.getCurrentLoggedUser();
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void exists_anonymousAccessDenied() {
        userService.exists(TestUtil.ID_ONE);
    }

    @WithMockUser
    @Test
    public void exists_userAuthenticatedAccess() {
        userService.exists(TestUtil.ID_ONE);
    }

    @WithCustomMockUser(authorities = TestUtil.ANONYMOUS)
    @Test(expected = AccessDeniedException.class)
    public void changeUserPasswordById_anonymousAccessDenied() {
        userService.changeUserPasswordById(TestUtil.ID_ONE, "newPassword");
    }

    @WithCustomMockUser(id = TestUtil.ID_ONE)
    @Test(expected = AccessDeniedException.class)
    public void changeUserPasswordById_wrongUserAccessDenied() {
        userService.changeUserPasswordById(TestUtil.ID_TWO, "newPassword");
    }

    @WithCustomMockUser(id = TestUtil.ID_TWO, authorities = TestUtil.USER)
    @Test
    public void changeUserPasswordById_userAuthenticatedAccess() {
        userService.changeUserPasswordById(TestUtil.ID_TWO, "newPassword");
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowingCountById_anonymousAccessDenied() {
        userService.getUserFollowingCountById(TestUtil.ID_ONE);
    }

    @WithMockUser
    @Test
    public void getUserFollowingCountById_userAuthenticatedAccess() {
        userService.getUserFollowingCountById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getUserFollowingsById_anonymousAccessDenied() {
        userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithMockUser
    @Test
    public void getUserFollowingsById_userAuthenticatedAccess() {
        userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test
    public void activateAccount_anonymousAccessDenied() {
        userService.activateAccount("secret_activation_key");
    }


    @WithAnonymousUser
    @Test
    public void loadUserByUsername_anonymousAccessDenied() {
        userService.loadUserByUsername("username");
    }

    @WithAnonymousUser
    @Test
    public void create_anonymousAccessDenied() {
        userService.create(a(user()));
    }
}