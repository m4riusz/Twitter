package com.twitter.service;

import com.twitter.dao.UserVoteDao;
import com.twitter.exception.UserVoteException;
import com.twitter.model.AbstractPost;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.dto.PostVote;
import com.twitter.util.MessageUtil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by mariusz on 08.08.16.
 */

abstract class PostServiceImpl<T extends AbstractPost, TRepository extends CrudRepository<T, Long>> implements PostService<T> {

    protected final TRepository repository;
    private final UserService userService;
    private final UserVoteDao userVoteDao;

    PostServiceImpl(TRepository repository, UserService userService, UserVoteDao userVoteDao) {
        this.repository = repository;
        this.userService = userService;
        this.userVoteDao = userVoteDao;
    }

    @Override
    public T create(@Param("post") T post) {
        return repository.save(post);
    }

    @Override
    public void delete(long postId) {
        checkIfPostExists(postId);
        T post = getById(postId);
        User currentLoggedUser = userService.getCurrentLoggedUser();
        if (post.getOwner().equals(currentLoggedUser)) {
            repository.delete(postId);
        }
    }

    @Override
    public boolean exists(long postId) {
        return repository.exists(postId);
    }

    @Override
    public T getById(long postId) {
        checkIfPostExists(postId);
        return repository.findOne(postId);
    }

    @Override
    public UserVote vote(PostVote postVote) {
        User user = userService.getCurrentLoggedUser();
        T post = getById(postVote.getPostId());
        UserVote userVote = userVoteDao.findByUserAndAbstractPost(user, post);
        if (userVote == null) {
            userVote = new UserVote(postVote.getVote(), user, post);
            userVoteDao.save(userVote);
        } else {
            userVote.setVote(postVote.getVote());
        }
        return userVote;
    }

    @Override
    public void deleteVote(long voteId) {
        User user = userService.getCurrentLoggedUser();
        UserVote userVote = userVoteDao.findOne(voteId);
        checkIfUserVoteExist(voteId);
        if (userVote.getUser() != user) {
            throw new UserVoteException(MessageUtil.VOTE_DELETE_ERROR_MSG);
        }
        userVoteDao.delete(userVote);
    }

    private void checkIfPostExists(long postID) {
        getById(postID);
    }

    private void checkIfUserVoteExist(long userVoteId) {
        if (!userVoteDao.exists(userVoteId)) {
            throw new UserVoteException(MessageUtil.VOTE_DOES_NOT_EXIST_ERROR_MSG);
        }
    }

    boolean doesUserExist(long userId) {
        return userService.exists(userId);
    }

}
