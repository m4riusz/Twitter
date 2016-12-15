package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.dto.UserCreateForm;
import com.twitter.exception.*;
import com.twitter.model.Avatar;
import com.twitter.model.Password;
import com.twitter.model.Role;
import com.twitter.model.User;
import com.twitter.util.AvatarUtil;
import com.twitter.util.MessageUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, JavaMailSender javaMailSender, AvatarUtil avatarUtil, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.javaMailSender = javaMailSender;
        this.avatarUtil = avatarUtil;
        this.passwordEncoder = passwordEncoder;
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
    public User create(UserCreateForm userCreateForm) throws IOException {
        if (exists(userCreateForm.getUsername())) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_USERNAME_ERROR_MSG);
        } else if (userWithEmailExists(userCreateForm.getEmail())) {
            throw new UserAlreadyExistsException(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG);
        }
        String verifyKey = UUID.randomUUID().toString();
        sendEmail(userCreateForm.getEmail(),
                MessageUtil.EMAIL_FROM,
                MessageUtil.EMAIL_SUBJECT,
                MessageUtil.EMAIL_CONTENT + "\n" + MessageUtil.EMAIL_VERIFY_LINK + verifyKey
        );
        User user = new User();
        user.getAccountStatus().setVerifyKey(verifyKey);
        user.setAvatar(avatarUtil.getDefaultAvatar());
        user.setUsername(userCreateForm.getUsername());
        user.setEmail(userCreateForm.getEmail());
        user.setGender(userCreateForm.getGender());
        user.setPassword(new Password(passwordEncoder.encode(userCreateForm.getPassword())));
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
        User user = getUserById(userId);
        user.getAccountStatus().setDeleted(true);
    }

    @Override
    public User changeUserPasswordById(long userId, String password) {
        User userToChange = getUserById(userId);
        userToChange.setPassword(new Password(passwordEncoder.encode(password)));
        //// TODO: 13.12.16 podmienic security contex na usera i tam gdzie zmienia sie user
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
        getCurrentLoggedUser().setAvatar(avatar);
        return user.getAvatar();
    }

    @Override
    public Boolean isFollowed(long userId) {
        User user = getCurrentLoggedUser();
        User followed = getUserById(userId);
        return getUserFollowingsById(user.getId(), new PageRequest(0, Integer.MAX_VALUE)).contains(followed);
    }

    @Override
    public User changeUserEmail(long userId, String email) {
        User user = getUserById(userId);
        User userByEmail = userDao.findByEmail(email);
        if (userByEmail != null) {
            throw new UserException(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG);
        }
        user.setEmail(email);
        getCurrentLoggedUser().setEmail(email);
        sendEmail(user.getEmail(), MessageUtil.EMAIL_FROM, MessageUtil.EMAIL_SUBJECT, "You have changed email!");
        return user;
    }

    @Override
    public List<User> queryForUser(String username,Pageable pageable) {
        return userDao.findByUsernameIgnoreCaseLike(username, pageable);
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

    private boolean isBanDateBeforeNow(Date date) {
        return date.before(Calendar.getInstance().getTime());
    }
}
