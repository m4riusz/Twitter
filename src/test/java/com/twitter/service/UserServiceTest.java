package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.UserDao;
import com.twitter.dto.UserCreateForm;
import com.twitter.exception.*;
import com.twitter.model.*;
import com.twitter.util.AvatarUtil;
import com.twitter.util.TestUtil;
import freemarker.template.TemplateException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.twitter.builders.AvatarBuilder.avatar;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.UserFollowerMatcher.hasFollowers;
import static com.twitter.matchers.UserIsBanned.isBanned;
import static com.twitter.matchers.UserIsEnabled.isEnabled;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
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
public class UserServiceTest {

    @Mock
    private UserDao userDao;
    @Mock
    private EmailService emailService;
    @Mock
    private AvatarUtil avatarUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NotificationService notificationService;

    private UserService userService;

    private Authentication authentication;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userDao, avatarUtil, passwordEncoder, notificationService, emailService);
        authentication = mock(Authentication.class);
        mockStatic(SecurityContextHolder.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void getUserByIdTest_userExists() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        User userResult = userService.getUserById(TestUtil.ID_ONE);
        assertThat(userResult, is(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByIdTest_userDoesNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
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
    public void create_userCreateTest() throws IOException, MessagingException, TemplateException {
        User user = a(user());
        when(userDao.findByUsername(anyString())).thenReturn(null);
        when(userDao.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("ENCRYPTED_PASSWORD");
        when(userDao.save(any(User.class))).thenReturn(user);
        UserCreateForm userCreateForm = new UserCreateForm(user.getUsername(), user.getPassword(), user.getEmail(), user.getGender());
        User createResult = userService.create(userCreateForm);
        assertThat(createResult, is(user));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString(), anyString(), any(EmailType.class));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void create_usernameAlreadyExist() throws IOException, MessagingException, TemplateException {
        when(userDao.findByUsername(anyString())).thenReturn(a(user()));
        UserCreateForm userCreateForm = new UserCreateForm("user", "password", "email@email.com", Gender.FEMALE);
        userService.create(userCreateForm);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void create_emailAlreadyExist() throws IOException, MessagingException, TemplateException {
        User user = a(user());
        when(userDao.findByUsername(anyString())).thenReturn(null);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        UserCreateForm userCreateForm = new UserCreateForm(user.getUsername(), user.getPassword(), user.getEmail(), user.getGender());
        userService.create(userCreateForm);
    }

    @Test
    public void follow_userFollowsOtherUser() {
        User userOne = a(user().withId(TestUtil.ID_ONE));
        User userTwo = a(user().withId(TestUtil.ID_TWO));
        when(authentication.getPrincipal()).thenReturn(userTwo);
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userTwo);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(userOne);
        userService.follow(userOne.getId());
        assertThat(userOne, hasFollowers(userTwo));
    }

    @Test(expected = UserFollowException.class)
    public void follow_userFollowHimself() {
        User user = a(user().withId(TestUtil.ID_ONE));
        when(authentication.getPrincipal()).thenReturn(user);
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.follow(user.getId());
    }

    @Test(expected = UserFollowException.class)
    public void follow_userAlreadyFollowed() {
        User user = a(user()
                .withId(TestUtil.ID_ONE)
        );
        User userToFollow = a(user()
                .withId(TestUtil.ID_TWO)
                .withFollowers(aListWith(user)
                )
        );
        when(authentication.getPrincipal()).thenReturn(user);
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToFollow);
        userService.follow(userToFollow.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void follow_userToFollowDoesNotExists() {
        when(authentication.getPrincipal()).thenReturn(a(user()));
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.follow(TestUtil.ID_ONE);
    }

    @Test
    public void unfollow_userFollowsOtherUser() {
        User user = a(user()
                .withId(TestUtil.ID_ONE)
        );
        User userToUnfollow = a(user()
                .withId(TestUtil.ID_TWO)
                .withFollowers(aListWith(user)
                )
        );
        when(authentication.getPrincipal()).thenReturn(user);
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToUnfollow);
        userService.unfollow(userToUnfollow.getId());
        assertThat(userToUnfollow, not(hasFollowers(user)));
    }

    @Test(expected = UserUnfollowException.class)
    public void unfollow_userUnfollowsHimself() {
        User user = a(user());
        when(authentication.getPrincipal()).thenReturn(user);
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.unfollow(user.getId());
    }

    @Test(expected = UserUnfollowException.class)
    public void unfollow_userNotFollowed() {
        User user = a(user()
                .withId(TestUtil.ID_ONE)
        );
        User userToUnfollow = a(user()
                .withId(TestUtil.ID_TWO)
        );
        when(authentication.getPrincipal()).thenReturn(user);
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(TestUtil.ID_ONE)).thenReturn(user);
        when(userDao.findOne(TestUtil.ID_TWO)).thenReturn(userToUnfollow);
        userService.unfollow(userToUnfollow.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void unfollow_userToUnfollowDoesNotExists() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.unfollow(TestUtil.ID_ONE);
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUserById_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.deleteUserById(TestUtil.ID_ONE);
    }

    @Test
    public void deleteUserById_userExists() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.deleteUserById(TestUtil.ID_ONE);
        assertThat(user.getAccountStatus().isDeleted(), is(true));
    }

    @Test
    public void getAllUsersCount_noUsers() {
        when(userDao.count()).thenReturn(0L);
        Long allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount, is(equalTo(0L)));
    }

    @Test
    public void getAllUsersCount_someUsers() {
        when(userDao.count()).thenReturn(5L);
        Long allUsersCount = userService.getAllUsersCount();
        assertThat(allUsersCount, is(equalTo(5L)));
    }

    @Test
    public void getAllUsers_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(userOne, userTwo)));
        List<User> allUsers = userService.getAllUsers(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allUsers, is(aListWith(userOne, userTwo)));
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
        Long userFollowersCountById = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userFollowersCountById, is(equalTo(0L)));
    }

    @Test
    public void getUserFollowersCountById_userExistsSomeFollowers() {
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowersCountByUserId(TestUtil.ID_ONE)).thenReturn(2L);
        Long userFollowersById = userService.getUserFollowersCountById(TestUtil.ID_ONE);
        assertThat(userFollowersById, is(equalTo(2L)));
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
        List<User> userFollowersById = userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowersById, is(emptyList()));
    }

    @Test
    public void getUserFollowersById_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowersByUserId(anyLong(), any(Pageable.class))).thenReturn(aListWith(userOne, userTwo));
        List<User> userFollowersById = userService.getUserFollowersById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowersById, is(aListWith(userOne, userTwo)));
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
        Long userFollowingCountById = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userFollowingCountById, is(equalTo(0L)));
    }

    @Test
    public void getUserFollowingCountById_userExistsSomeFollowers() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findFollowingCountByUserId(anyLong())).thenReturn(2L);
        Long userFollowersById = userService.getUserFollowingCountById(TestUtil.ID_ONE);
        assertThat(userFollowersById, is(equalTo(2L)));
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
        List<User> userFollowingsById = userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowingsById, is(emptyList()));
    }

    @Test
    public void getUserFollowingsById_someUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        when(userDao.exists(TestUtil.ID_ONE)).thenReturn(true);
        when(userDao.findFollowingByUserId(anyLong(), any(Pageable.class))).thenReturn(aListWith(userOne, userTwo));
        List<User> userFollowingsById = userService.getUserFollowingsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFollowingsById, is(aListWith(userOne, userTwo)));

    }

    @Test(expected = UserException.class)
    public void activateAccount_verifyKeyNotFound() {
        when(userDao.findOneByAccountStatusVerifyKey(anyString())).thenReturn(null);
        userService.activateAccount("someKey");
    }

    @Test(expected = UserException.class)
    public void activateAccount_accountAlreadyActivated() {
        User user = a(user().withAccountStatus(new AccountStatus(true, "someKey")));
        when(userDao.findOneByAccountStatusVerifyKey(anyString())).thenReturn(user);
        userService.activateAccount("someKey");
    }

    @Test
    public void activateAccount_accountNotActivated() {
        User user = a(user().withAccountStatus(new AccountStatus(false, "someKey")));
        when(userDao.findOneByAccountStatusVerifyKey(anyString())).thenReturn(user);
        userService.activateAccount("someKey");
        assertThat(user, isEnabled(true));
    }

    @Test(expected = UserNotFoundException.class)
    public void banUser_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.banUser(TestUtil.ID_ONE, DateTime.now().toDate());
    }

    @Test(expected = TwitterDateException.class)
    public void banUser_invalidDate() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.banUser(user.getId(), null);
        assertThat(user, isBanned(false));
    }

    @Test(expected = TwitterDateException.class)
    public void banUser_dateBeforeNow() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.banUser(user.getId(), TestUtil.DATE_2003);
        assertThat(user, isBanned(false));
    }

    @Test
    public void banUser_userExist() {
        User user = a(user());
        Date futureDate = DateTime.now().plusDays(10).toDate();
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.banUser(user.getId(), futureDate);
        assertThat(user, isBanned(true));
    }

    @Test(expected = UserNotFoundException.class)
    public void unbanUser_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.unbanUser(TestUtil.ID_ONE);
    }

    @Test
    public void unbanUser_userExist() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.unbanUser(user.getId());
        assertThat(user, isBanned(false));
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserRole_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.changeUserRole(1L, Role.MODERATOR);
    }

    @Test
    public void changeUserRole_userExists() {
        User user = a(user().withRole(Role.USER));
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        userService.changeUserRole(user.getId(), Role.MODERATOR);
        assertThat(user.getRole(), is(Role.MODERATOR));
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserPasswordById_userDoesNotExist() {
        when(userDao.findOne(anyLong())).thenReturn(null);
        userService.changeUserPasswordById(1L, "NewPass");
    }

    @Test
    public void changeUserPasswordById_userExists() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("newPassword");
        userService.changeUserPasswordById(user.getId(), "newPassword");
        assertThat(user.getPassword(), is("newPassword"));
    }

    @Test
    public void getCurrentLoggedUser_userNotFound() {
        when(authentication.getPrincipal()).thenReturn(null);
        User currentLoggedUser = userService.getCurrentLoggedUser();
        assertThat(currentLoggedUser, is(nullValue()));
    }

    @Test
    public void getCurrentLoggedUser_userFound() {
        User user = a(user());
        when(authentication.getPrincipal()).thenReturn(user);
        User currentLoggedUser = userService.getCurrentLoggedUser();
        assertThat(currentLoggedUser, is(user));
    }

    @Test
    public void exists_userNotExists() {
        when(userDao.exists(anyLong())).thenReturn(false);
        boolean userExist = userService.exists(TestUtil.ID_ONE);
        assertThat(userExist, is(false));
    }

    @Test
    public void exists_userExists() {
        when(userDao.exists(anyLong())).thenReturn(true);
        boolean userExist = userService.exists(TestUtil.ID_ONE);
        assertThat(userExist, is(true));
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserAvatar_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.getUserAvatar(TestUtil.ID_ONE);
    }

    @Test
    public void getUserAvatar_userExists() {
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        Avatar userAvatar = userService.getUserAvatar(TestUtil.ID_ONE);
        assertThat(userAvatar, is(user.getAvatar()));
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserAvatar_userDoesNotExist() throws IOException {
        when(userDao.exists(anyLong())).thenReturn(false);
        userService.changeUserAvatar(TestUtil.ID_ONE, a(avatar()));
    }

    @Test
    public void changeUserAvatar_userExists() throws IOException {
        String newFileName = "file.jpg";
        int avatarSize = 100;
        Avatar newAvatar = a(avatar()
                .withFileName(newFileName)
                .withBytes(new byte[avatarSize])
        );
        User user = a(user());
        when(userDao.exists(anyLong())).thenReturn(true);
        when(userDao.findOne(anyLong())).thenReturn(user);
        when(avatarUtil.resizeToStandardSize(any(Avatar.class))).thenReturn(newAvatar);
        Avatar changedUserAvatar = userService.changeUserAvatar(TestUtil.ID_ONE, user.getAvatar());
        assertThat(changedUserAvatar.getFileName(), is(newFileName));
        assertThat(changedUserAvatar.getBytes().length, is(avatarSize));
    }

    @Test(expected = UserNotFoundException.class)
    public void isFollowed_userDoesNotExist() {
        User loggedUser = a(user().withId(TestUtil.ID_ONE));
        when(authentication.getPrincipal()).thenReturn(loggedUser);
        when(userDao.exists(TestUtil.ID_TWO)).thenReturn(false);
        userService.isFollowed(TestUtil.ID_TWO);
    }

    @Test
    public void isFollowed_loggedUserFollowingUser() {
        User loggedUser = a(user().withId(TestUtil.ID_ONE));
        User user = a(user().withId(TestUtil.ID_ONE));
        when(authentication.getPrincipal()).thenReturn(loggedUser);
        when(userDao.exists(user.getId())).thenReturn(true);
        when(userDao.exists(loggedUser.getId())).thenReturn(true);
        when(userDao.findOne(user.getId())).thenReturn(user);
        when(userDao.findFollowingByUserId(loggedUser.getId(), new PageRequest(0, Integer.MAX_VALUE))).thenReturn(aListWith(user));
        Boolean followed = userService.isFollowed(user.getId());
        assertThat(followed, is(true));
    }

    @Test
    public void isFollowed_loggedUserIsNotFollowingUser() {
        User loggedUser = a(user().withId(TestUtil.ID_ONE));
        User user = a(user().withId(TestUtil.ID_ONE));
        when(authentication.getPrincipal()).thenReturn(loggedUser);
        when(userDao.exists(user.getId())).thenReturn(true);
        when(userDao.exists(loggedUser.getId())).thenReturn(true);
        when(userDao.findOne(user.getId())).thenReturn(user);
        when(userDao.findFollowingByUserId(loggedUser.getId(), TestUtil.ALL_IN_ONE_PAGE)).thenReturn(emptyList());
        Boolean followed = userService.isFollowed(user.getId());
        assertThat(followed, is(false));
    }

    @Test(expected = UserNotFoundException.class)
    public void changeUserEmail_userDoesNotExist() throws MessagingException {
        User user = a(user());
        when(userDao.exists(user.getId())).thenReturn(false);
        userService.changeUserEmail(user.getId(), "some@email.com");
    }

    @Test(expected = UserException.class)
    public void changeUserEmail_userExistsEmailIsAlreadyTaken() throws MessagingException {
        User user = a(user());
        when(userDao.exists(user.getId())).thenReturn(true);
        when(userDao.findByEmail(anyString())).thenReturn(a(user()));
        userService.changeUserEmail(user.getId(), "some@email.com");
    }

    @Test
    public void changeUserEmail_userExistsEmailIsFree() throws MessagingException {
        String newEmail = "myNew@email.com";
        String oldEmail = "old@email.com";
        User user = a(user().withEmail(oldEmail));
        when(userDao.exists(user.getId())).thenReturn(true);
        when(userDao.findOne(user.getId())).thenReturn(user);
        when(userDao.findByEmail(anyString())).thenReturn(null);
        userService.changeUserEmail(user.getId(), newEmail);
        assertThat(user.getEmail(), is(newEmail));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString(), anyString(), any(EmailType.class));
    }

    @Test
    public void queryForUser_usernameWithUserPrefix() {
        userService.queryForUser("@USER_ONE", TestUtil.ALL_IN_ONE_PAGE);
        verify(userDao, times(1)).findByUsernameStartingWithIgnoreCase("USER_ONE", TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void queryForUser_usernameWithoutUserPrefix() {
        userService.queryForUser("USER_ONE", TestUtil.ALL_IN_ONE_PAGE);
        verify(userDao, times(1)).findByUsernameStartingWithIgnoreCase("USER_ONE", TestUtil.ALL_IN_ONE_PAGE);
    }

}


