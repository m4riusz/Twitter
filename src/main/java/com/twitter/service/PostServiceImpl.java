package com.twitter.service;

import com.twitter.dao.UserVoteDao;
import com.twitter.model.AbstractPost;
import com.twitter.model.Result;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.util.MessageUtil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import static com.twitter.model.Result.ResultFailure;
import static com.twitter.model.Result.ResultSuccess;

/**
 * Created by mariusz on 08.08.16.
 */

public abstract class PostServiceImpl<T extends AbstractPost, TRepository extends CrudRepository<T, Long>> implements PostService<T> {

    protected final TRepository repository;
    private final UserService userService;
    private final UserVoteDao userVoteDao;

    PostServiceImpl(TRepository repository, UserService userService, UserVoteDao userVoteDao) {
        this.repository = repository;
        this.userService = userService;
        this.userVoteDao = userVoteDao;
    }

    @Override
    public Result<Boolean> create(@Param("post") T post) {
        if (repository.save(post) != null) {
            return ResultSuccess(true);
        }
        return ResultFailure(MessageUtil.SAVE_COMMENT_ERROR);
    }

    @Override
    public Result<Boolean> delete(long postId) {
        if (repository.exists(postId)) {
            repository.delete(postId);
            return ResultSuccess(true);
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public boolean exists(long postId) {
        return repository.exists(postId);
    }

    @Override
    public Result<T> getById(long postId) {
        if (repository.exists(postId)) {
            return ResultSuccess(repository.findOne(postId));
        }
        return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> vote(UserVote userVote) {
        if (!doesUserExist(userVote.getUser().getId())) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesPostExist(userVote.getAbstractPost().getId())) {
            return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (doesVoteExist(userVote)) {
            return ResultFailure(MessageUtil.POST_ALREADY_VOTED);
        }
        userVoteDao.save(userVote);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> deleteVote(long voteId) {
        User user = userService.getCurrentLoggedUser();
        UserVote userVote = userVoteDao.findOne(voteId);
        if (!userVoteDao.exists(voteId)) {
            return ResultFailure(MessageUtil.VOTE_DOES_NOT_EXIST_ERROR_MSG);
        } else if (userVote.getUser() != user) {
            return ResultFailure(MessageUtil.VOTE_DELETE_ERROR_MSG);
        }
        userVoteDao.delete(userVote);
        return ResultSuccess(true);
    }

    @Override
    public Result<Boolean> changeVote(UserVote userVote) {
        if (!doesUserExist(userVote.getUser().getId())) {
            return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesPostExist(userVote.getAbstractPost().getId())) {
            return ResultFailure(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        } else if (!doesVoteExist(userVote)) {
            return ResultFailure(MessageUtil.NOT_VOTE_ERROR_MSG);
        }
        UserVote userVoteFromDb = userVoteDao.findByUserAndAbstractPost(userVote.getUser(), userVote.getAbstractPost());
        userVoteFromDb.setVote(userVote.getVote());
        return ResultSuccess(true);
    }

    protected boolean doesPostExist(long postId) {
        return repository.exists(postId);
    }

    protected boolean doesUserExist(long userId) {
        return userService.exists(userId);
    }

    private boolean doesVoteExist(UserVote userVote) {
        return userVoteDao.findByUserAndAbstractPost(userVote.getUser(), userVote.getAbstractPost()) != null;
    }
}
