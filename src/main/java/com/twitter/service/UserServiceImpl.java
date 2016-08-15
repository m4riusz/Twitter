package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.exception.*;
import com.twitter.model.Avatar;
import com.twitter.model.Password;
import com.twitter.model.Role;
import com.twitter.model.User;
import com.twitter.util.AvatarUtil;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private JavaMailSender javaMailSender;
    private AvatarUtil avatarUtil;

    @Autowired
    public UserServiceImpl(UserDao userDao, JavaMailSender javaMailSender, AvatarUtil avatarUtil) {
        this.userDao = userDao;
        this.javaMailSender = javaMailSender;
        this.avatarUtil = avatarUtil;
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
    public User getUserById(long userId) {
        checkIfUserExist(userId);
        return userDao.findOne(userId);
    }

    @Override
    public User create(User user) throws IOException {
        if (userWithUsernameExists(user.getUsername())) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_USERNAME_ERROR_MSG);
        } else if (userWithEmailExists(user.getEmail())) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG);
        }
        String verifyKey = UUID.randomUUID().toString();
        sendEmail(user.getEmail(),
                MessageUtil.EMAIL_FROM,
                MessageUtil.EMAIL_SUBJECT,
                MessageUtil.EMAIL_CONTENT + "\n" + MessageUtil.EMAIL_VERIFY_LINK + verifyKey
        );
        user.getAccountStatus().setVerifyKey(verifyKey);
        user.setAvatar(avatarUtil.getDefaultAvatar());
        return userDao.save(user);
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
    }

    @Override
    public void unbanUser(long userToUnbanId) {
        User userToUnban = getUserById(userToUnbanId);
        userToUnban.getAccountStatus().setBannedUntil(null);
    }

    @Override
    public User changeUserRole(long userToChangeId, Role role) {
        User userToChange = getUserById(userToChangeId);
        userToChange.setRole(role);
        return userToChange;
    }

    @Override
    public void deleteUserById(long userId) {
        checkIfUserExist(userId);
        userDao.delete(userId);
    }

    @Override
    public User changeUserPasswordById(long userId, String password) {
        User userToChange = getUserById(userId);
        userToChange.setPassword(new Password(password));
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
        user.setAvatar(avatarUtil.resizeToStandardSize(avatar));
        return user.getAvatar();
    }

    @Override
    public void activateAccount(String verifyKey) {
        User user = userDao.findOneByAccountStatusVerifyKey(verifyKey);
        if (userIsNotActivated(user)) {
            user.getAccountStatus().setEnable(true);
            return;
        } else if (userIsActivated(user)) {
            throw new UserException(MessageUtil.ACCOUNT_HAS_BEEN_ALREADY_ENABLED);
        }
        throw new UserException(MessageUtil.INVALID_VERIFY_KEY);
    }

    private boolean userIsActivated(User user) {
        return user != null && user.getAccountStatus().isEnable();
    }

    private boolean userIsNotActivated(User user) {
        return user != null && !user.getAccountStatus().isEnable();
    }

    private void sendEmail(String to, String from, String subject, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom(from);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        javaMailSender.send(mailMessage);
    }

    private void checkIfUserExist(long userId) {
        if (!exists(userId)) {
            throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
    }

    private boolean userWithEmailExists(String email) {
        return userDao.findByEmail(email) != null;
    }

    private boolean userWithUsernameExists(String username) {
        return userDao.findByUsername(username) != null;
    }
    private boolean isBanDateBeforeNow(Date date) {
        return date.before(Calendar.getInstance().getTime());
    }
}
