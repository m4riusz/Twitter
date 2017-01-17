package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.dto.UserCreateForm;
import com.twitter.exception.*;
import com.twitter.model.*;
import com.twitter.util.AvatarUtil;
import com.twitter.util.MessageUtil;
import freemarker.template.TemplateException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USER_PREFIX = "@";
    private UserDao userDao;
    private AvatarUtil avatarUtil;
    private PasswordEncoder passwordEncoder;
    private NotificationService notificationService;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserDao userDao, AvatarUtil avatarUtil, PasswordEncoder passwordEncoder, @Lazy NotificationService notificationService, EmailService emailService) {
        this.userDao = userDao;
        this.avatarUtil = avatarUtil;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_USERNAME_ERROR_MSG);
        }
        return user;
    }

    @Override
    public User getCurrentLoggedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public boolean exists(long userId) {
        return userDao.exists(userId);
    }

    @Override
    public boolean exists(String username) {
        return userDao.findByUsername(username) != null;
    }

    @Override
    public User getUserById(long userId) {
        checkIfUserExist(userId);
        return userDao.findOne(userId);
    }

    @Override
    public User create(UserCreateForm userCreateForm) throws IOException, MessagingException, TemplateException {
        if (exists(userCreateForm.getUsername())) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_USERNAME_ERROR_MSG);
        } else if (userWithEmailExists(userCreateForm.getEmail())) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG);
        }
        String verifyKey = UUID.randomUUID().toString();
        User user = new User();
        user.getAccountStatus().setVerifyKey(verifyKey);
        user.setAvatar(avatarUtil.getDefaultAvatar());
        user.setUsername(userCreateForm.getUsername());
        user.setEmail(userCreateForm.getEmail());
        user.setGender(userCreateForm.getGender());
        user.setPassword(new Password(passwordEncoder.encode(userCreateForm.getPassword())));
        User createdUser = userDao.save(user);

        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("verifyLink", MessageUtil.EMAIL_VERIFY_LINK + user.getAccountStatus().getVerifyKey());

        emailService.sendEmail(userCreateForm.getEmail(), MessageUtil.EMAIL_FROM, MessageUtil.EMAIL_SUBJECT, "create_user_email.ftl", model, EmailType.TEXT_HTML);

        return createdUser;
    }


    @Override
    public void follow(long userToFollowId) {
        User user = getUserById(getCurrentLoggedUser().getId());
        User userToFollow = getUserById(userToFollowId);
        if (user.getId() == userToFollowId) {
            throw new UserFollowException(MessageUtil.FOLLOW_YOURSELF_ERROR_MSG);
        } else if (userToFollow.getFollowers().contains(user)) {
            throw new UserFollowException(MessageUtil.FOLLOW_ALREADY_FOLLOWED_ERROR_MSG);
        }
        userToFollow.getFollowers().add(user);
        notifyUser(user, userToFollow, user.getUsername() + " is now following you!");
    }

    @Override
    public void unfollow(long userToUnfollowId) {
        User user = getCurrentLoggedUser();
        User userToUnfollow = getUserById(userToUnfollowId);
        if (user.getId() == userToUnfollow.getId()) {
            throw new UserUnfollowException(MessageUtil.UNFOLLOW_YOURSELF_ERROR_MSG);
        } else if (!userToUnfollow.getFollowers().contains(user)) {
            throw new UserUnfollowException(MessageUtil.UNFOLLOW_UNFOLLOWED_ERROR_MSG);
        }
        userToUnfollow.getFollowers().remove(user);
    }

    @Override
    public void banUser(long userToBanId, Date date) {
        User userToBan = getUserById(userToBanId);
        if (date == null) {
            throw new TwitterDateException(MessageUtil.DATE_IS_NOT_SET);
        } else if (isBanDateBeforeNow(date)) {
            throw new TwitterDateException(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG);
        }
        userToBan.getAccountStatus().setBannedUntil(date);
        notifyUser(null, userToBan, "Your's account has been banned until " + DateFormat.getInstance().format(date) + "!");
    }

    @Override
    public void unbanUser(long userToUnbanId) {
        User userToUnban = getUserById(userToUnbanId);
        userToUnban.getAccountStatus().setBannedUntil(null);
        notifyUser(null, userToUnban, "Your's account has been unlocked!");
    }

    @Override
    public User changeUserRole(long userToChangeId, Role role) {
        User userToChange = getUserById(userToChangeId);
        User currentUser = getCurrentLoggedUser();
        Role currentRole = userToChange.getRole();
        if (currentRole != role) {
            userToChange.setRole(role);
            if (userIsPromoted(currentRole, role)) {
                notifyUser(currentUser, userToChange, "You have been promoted to " + role.getAuthority());
            } else {
                notifyUser(currentUser, userToChange, "You have been demoted to " + role.getAuthority());
            }
        }
        return userToChange;
    }

    private boolean userIsPromoted(Role actualRole, Role newRole) {
        if (actualRole == Role.USER && (newRole == Role.MODERATOR || newRole == Role.ADMIN)) {
            return true;
        } else if (actualRole == Role.MODERATOR && newRole == Role.ADMIN) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteUserById(long userId) {
        User user = getUserById(userId);
        user.getAccountStatus().setDeleted(true);
    }

    @Override
    public User changeUserPasswordById(long userId, String password) throws TemplateException, IOException, MessagingException {
        User userToChange = getUserById(userId);
        if (passwordEncoder.matches(password, userToChange.getPassword())) {
            throw new UserException(MessageUtil.USER_SAME_PASSWORD_ERROR_MSG);
        }
        userToChange.setPassword(new Password(passwordEncoder.encode(password)));
        Map<String, Object> model = new HashMap<>();
        model.put("user", userToChange);
        emailService.sendEmail(userToChange.getEmail(), MessageUtil.EMAIL_FROM, MessageUtil.EMAIL_SUBJECT, "password_change.ftl", model, EmailType.TEXT_HTML);
        return userToChange;
    }

    @Override
    public Long getAllUsersCount() {
        return userDao.count();
    }

    @Override
    public List<User> getAllUsers(Pageable pageable) {
        return userDao.findAll(pageable).getContent();
    }

    @Override
    public Long getUserFollowersCountById(long userId) {
        checkIfUserExist(userId);
        return userDao.findFollowersCountByUserId(userId);
    }

    @Override
    public List<User> getUserFollowersById(long userId, Pageable pageable) {
        checkIfUserExist(userId);
        return userDao.findFollowersByUserId(userId, pageable);
    }

    @Override
    public Long getUserFollowingCountById(long userId) {
        checkIfUserExist(userId);
        return userDao.findFollowingCountByUserId(userId);
    }

    @Override
    public List<User> getUserFollowingsById(long userId, Pageable pageable) {
        checkIfUserExist(userId);
        return userDao.findFollowingByUserId(userId, pageable);
    }

    @Override
    public Avatar getUserAvatar(long userId) {
        return getUserById(userId).getAvatar();
    }

    @Override
    public Avatar changeUserAvatar(long userId, Avatar avatar) throws IOException {
        User user = getUserById(userId);
        User currentLoggedUser = getCurrentLoggedUser();
        user.setAvatar(avatarUtil.resizeToStandardSize(avatar));
        if (user.equals(currentLoggedUser)) {
            currentLoggedUser.setAvatar(avatar);
        }
        return user.getAvatar();
    }

    @Override
    public Boolean isFollowed(long userId) {
        User user = getCurrentLoggedUser();
        User followed = getUserById(userId);
        return getUserFollowingsById(user.getId(), new PageRequest(0, Integer.MAX_VALUE)).contains(followed);
    }

    @Override
    public User changeUserEmail(long userId, String email) throws MessagingException, IOException, TemplateException {
        User user = getUserById(userId);
        User userByEmail = userDao.findByEmail(email);
        User currentLoggedUser = getCurrentLoggedUser();
        if (currentLoggedUser.getEmail().equals(email)) {
            throw new UserException(MessageUtil.USER_SAME_EMAIL_CHANGE_ERROR_MSG);
        }
        if (userByEmail != null) {
            throw new UserException(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG);
        }
        user.setEmail(email);
        if (user.equals(currentLoggedUser)) {
            currentLoggedUser.setEmail(email);
            Map<String, Object> model = new HashMap<>();
            model.put("user", user);
            emailService.sendEmail(user.getEmail(), MessageUtil.EMAIL_FROM, MessageUtil.EMAIL_SUBJECT, "email_change.ftl", model, EmailType.TEXT_HTML);
        }
        return user;
    }

    @Override
    public List<User> queryForUser(String username, Pageable pageable) {
        return userDao.findByUsernameStartingWithIgnoreCase(username.startsWith(USER_PREFIX) ? username.substring(1) : username, pageable);
    }

    @Override
    public String activateAccount(String verifyKey) {
        User user = userDao.findOneByAccountStatusVerifyKey(verifyKey);
        if (userIsNotActivated(user)) {
            user.getAccountStatus().setEnable(true);
            user.getAccountStatus().setEnableDate(DateTime.now().toDate());
            return MessageUtil.ACCOUNT_HAS_BEEN_ENABLED;
        } else if (userIsActivated(user)) {
            throw new UserException(MessageUtil.ACCOUNT_HAS_BEEN_ALREADY_ENABLED);
        }
        throw new UserException(MessageUtil.INVALID_VERIFY_KEY);
    }

    private void notifyUser(User source, User recipient, String notificationText) {
        Notification notification = new Notification();
        notification.setSeen(false);
        notification.setText(notificationText);
        notification.setSourceUser(source);
        notification.setDestinationUser(recipient);
        notificationService.save(notification);
    }

    private boolean userIsActivated(User user) {
        return user != null && user.getAccountStatus().isEnable();
    }

    private boolean userIsNotActivated(User user) {
        return user != null && !user.getAccountStatus().isEnable();
    }

    private void checkIfUserExist(long userId) {
        if (!exists(userId)) {
            throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
    }

    private boolean userWithEmailExists(String email) {
        return userDao.findByEmail(email) != null;
    }

    private boolean isBanDateBeforeNow(Date date) {
        return date.before(Calendar.getInstance().getTime());
    }
}
