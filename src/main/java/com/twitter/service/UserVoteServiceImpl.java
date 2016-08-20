package com.twitter.service;

import com.twitter.dao.UserVoteDao;
import com.twitter.exception.UserVoteException;
import com.twitter.exception.UserVoteNotFoundException;
import com.twitter.model.AbstractPost;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mariusz on 16.08.16.
 */

@Service
@Transactional
public class UserVoteServiceImpl implements UserVoteService {

    private UserVoteDao userVoteDao;
    private UserService userService;

    @Autowired
    public UserVoteServiceImpl(UserVoteDao userVoteDao, UserService userService) {
        this.userVoteDao = userVoteDao;
        this.userService = userService;
    }

    @Override
    public UserVote getById(long userVoteId) {
        if (exists(userVoteId)) {
            return userVoteDao.findOne(userVoteId);
        }
        throw new UserVoteNotFoundException(MessageUtil.VOTE_DOES_NOT_EXIST_ERROR_MSG);
    }

    @Override
    public UserVote findUserVoteForPost(User user, AbstractPost abstractPost) {
        return userVoteDao.findByUserAndAbstractPost(user, abstractPost);
    }

    @Override
    public boolean exists(long userVoteId) {
        return userVoteDao.exists(userVoteId);
    }

    @Override
    public UserVote save(UserVote userVote) {
        return userVoteDao.save(userVote);
    }

    @Override
    public void delete(long userVoteId) {
        UserVote userVote = getById(userVoteId);
        User currentLoggedUser = userService.getCurrentLoggedUser();
        if (!userVote.getUser().equals(currentLoggedUser)) {
            throw new UserVoteException(MessageUtil.VOTE_DELETE_ERROR_MSG);
        }
        userVoteDao.delete(userVote);
    }
}
