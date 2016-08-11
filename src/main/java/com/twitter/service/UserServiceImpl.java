package com.twitter.service;

import com.twitter.dao.UserDao;
import com.twitter.model.Password;
import com.twitter.model.Result;
import com.twitter.model.Role;
import com.twitter.model.User;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.twitter.model.Result.ResultFailure;
import static com.twitter.model.Result.ResultSuccess;

/**
 * Created by mariusz on 14.07.16.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private JavaMailSender javaMailSender;

    @Autowired
    public UserServiceImpl(UserDao userDao, JavaMailSender javaMailSender) {
        this.userDao = userDao;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
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
    public Result<User> getUserById(long userId) {
        User userByID = userDao.findOne(userId);
        if (userByID == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        return ResultSuccess(userByID);
    }

    @Override
    public Result<Boolean> create(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return ResultFailure(MessageUtil.USER_ALREADY_EXISTS_USERNAME_ERROR_MSG);
        } else if (userDao.findByEmail(user.getEmail()) != null) {
            return ResultFailure(MessageUtil.USER_ALREADY_EXISTS_EMAIL_ERROR_MSG);
        }
        String verifyKey = UUID.randomUUID().toString();
        sendEmail(user.getEmail(),
                MessageUtil.EMAIL_FROM,
                MessageUtil.EMAIL_SUBJECT,
                MessageUtil.EMAIL_CONTENT + "\n" + MessageUtil.EMAIL_VERIFY_LINK + verifyKey
        );
        user.getAccountStatus().setVerifyKey(verifyKey);
        userDao.save(user);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> follow(long userToFollowId) {
        User user = userDao.findOne(getCurrentLoggedUser().getId());
        User userToFollow = userDao.findOne(userToFollowId);
        if (userToFollow == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (user.getId() == userToFollowId) {
            return ResultFailure(MessageUtil.FOLLOW_YOURSELF_ERROR_MSG);
        } else if (userToFollow.getFollowers().contains(user)) {
            return ResultFailure(MessageUtil.FOLLOW_ALREADY_FOLLOWED_ERROR_MSG);
        }
        userToFollow.getFollowers().add(user);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> unfollow(long userToUnfollowId) {
        User user = getCurrentLoggedUser();
        User userToUnfollow = userDao.findOne(userToUnfollowId);
        if (userToUnfollow == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (user.getId() == userToUnfollow.getId()) {
            return ResultFailure(MessageUtil.UNFOLLOW_YOURSELF_ERROR_MSG);
        } else if (!userToUnfollow.getFollowers().contains(user)) {
            return ResultFailure(MessageUtil.UNFOLLOW_UNFOLLOWED_ERROR_MSG);
        }
        userToUnfollow.getFollowers().remove(user);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> banUser(long userToBanId, Date date) {
        User userToBan = userDao.findOne(userToBanId);
        if (userToBan == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (date == null) {
            return ResultFailure(MessageUtil.REPORT_DATE_NOT_SET_ERROR_MSG);
        } else if (isBanDateBeforeNow(date)) {
            return ResultFailure(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG);
        }
        userToBan.getAccountStatus().setBannedUntil(date);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> unbanUser(long userToUnbanId) {
        User userToUnban = userDao.findOne(userToUnbanId);
        if (userToUnban == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        userToUnban.getAccountStatus().setBannedUntil(null);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> changeUserRole(long userToChangeId, Role role) {
        User userToChange = userDao.findOne(userToChangeId);
        if (userToChange == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        userToChange.setRole(role);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> deleteUserById(long userId) {
        if (userDao.exists(userId)) {
            userDao.delete(userId);
            return ResultSuccess(true);
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> changeUserPasswordById(long userId, String password) {
        User userToChange = userDao.findOne(userId);
        if (userToChange == null) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
        userToChange.setPassword(new Password(password));
        return ResultSuccess(true);
    }

    @Override
    public Result<Long> getAllUsersCount() {
        return ResultSuccess(userDao.count());
    }

    @Override
    public Result<List<User>> getAllUsers(Pageable pageable) {
        return ResultSuccess(userDao.findAll(pageable).getContent());
    }

    @Override
    public Result<Long> getUserFollowersCountById(long userId) {
        if (userDao.exists(userId)) {
            return ResultSuccess(userDao.findFollowersCountByUserId(userId));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<User>> getUserFollowersById(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return ResultSuccess(userDao.findFollowersByUserId(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Long> getUserFollowingCountById(long userId) {
        if (userDao.exists(userId)) {
            return ResultSuccess(userDao.findFollowingCountByUserId(userId));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<User>> getUserFollowingsById(long userId, Pageable pageable) {
        if (userDao.exists(userId)) {
            return ResultSuccess(userDao.findFollowingByUserId(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> activateAccount(String verifyKey) {
        User user = userDao.findOneByAccountStatusVerifyKey(verifyKey);
        if (user != null && !user.getAccountStatus().isEnable()) {
            user.getAccountStatus().setEnable(true);
            return ResultSuccess(true);
        } else if (user != null && user.getAccountStatus().isEnable()) {
            return ResultFailure(MessageUtil.ACCOUNT_HAS_BEEN_ALREADY_ENABLED);
        }
        return ResultFailure(MessageUtil.INVALID_VERIFY_KEY);
    }

    private void sendEmail(String to, String from, String subject, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom(from);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        javaMailSender.send(mailMessage);
    }

    private boolean isBanDateBeforeNow(Date date) {
        return date.before(Calendar.getInstance().getTime());
    }
}
