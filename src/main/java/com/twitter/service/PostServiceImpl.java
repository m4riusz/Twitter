package com.twitter.service;

import com.twitter.dto.PostVote;
import com.twitter.exception.PostDeleteException;
import com.twitter.exception.PostNotFoundException;
import com.twitter.model.AbstractPost;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
import com.twitter.util.MessageUtil;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mariusz on 08.08.16.
 */
@Transactional
abstract class PostServiceImpl<T extends AbstractPost, TRepository extends CrudRepository<T, Long>> implements PostService<T> {

    protected final TRepository repository;
    protected final UserService userService;
    private final UserVoteService userVoteService;

    PostServiceImpl(TRepository repository, UserService userService, UserVoteService userVoteService) {
        this.repository = repository;
        this.userService = userService;
        this.userVoteService = userVoteService;
    }

    @Override
    public T create(@Param("post") T post) {
        post.setOwner(userService.getCurrentLoggedUser());
        return repository.save(post);
    }

    @Override
    public void delete(long postId) {
        T post = getById(postId);
        User currentLoggedUser = userService.getCurrentLoggedUser();
        if (isPostOwner(post, currentLoggedUser) && post.isDeleted()) {
            throw new PostDeleteException(MessageUtil.POST_ALREADY_DELETED);
        } else if (isPostOwner(post, currentLoggedUser)) {
            post.setContent(MessageUtil.DELETE_BY_OWNED_ABSTRACT_POST_CONTENT);
            post.setDeleted(true);
            return;
        }
        throw new PostDeleteException(MessageUtil.DELETE_NOT_OWN_POST);
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
        UserVote userVote = userVoteService.findUserVoteForPost(user, post);
        if (userVote == null) {
            userVote = new UserVote(postVote.getVote(), user, post);
            return userVoteService.save(userVote);
        } else {
            userVote.setVote(postVote.getVote());
        }
        return userVote;
    }

    @Override
    public void deleteVote(long tweetId) {
        User user = userService.getCurrentLoggedUser();
        T post = getById(tweetId);
        post.getVotes().stream()
                .filter(userVote -> userVote.getUser().equals(user))
                .findFirst()
                .ifPresent(userVote -> post.getVotes().remove(userVote));
    }

    @Override
    public UserVote getPostVote(long postId) {
        User user = userService.getCurrentLoggedUser();
        return userVoteService.findUserVoteForPost(user, repository.findOne(postId)) ;
    }

    @Override
    public long getPostVoteCount(long postId, Vote vote) {
        return userVoteService.getPostVoteCount(postId, vote);
    }

    protected void checkIfPostExists(long postId) {
        if (!repository.exists(postId)) {
            throw new PostNotFoundException(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
        }
    }

    boolean doesUserExist(long userId) {
        return userService.exists(userId);
    }

    private boolean isPostOwner(T post, User currentLoggedUser) {
        return post.getOwner().equals(currentLoggedUser);
    }

}
