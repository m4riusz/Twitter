package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.util.MessageUtil;
import com.twitter.util.SecurityUtil;
import com.twitter.util.TagExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.twitter.model.Result.ResultFailure;
import static com.twitter.model.Result.ResultSuccess;

/**
 * Created by mariusz on 29.07.16.
 */
@Service
@Transactional
public class TweetServiceImpl extends PostServiceImpl<Tweet, TweetDao> implements TweetService {

    private final TagExtractor tagExtractor;

    @Autowired
    public TweetServiceImpl(TweetDao tweetDao, UserService userService, UserVoteDao userVoteDao, TagExtractor tagExtractor) {
        super(tweetDao, userService, userVoteDao);
        this.tagExtractor = tagExtractor;
    }

    @Override
    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    public Result<Boolean> create(@Param("post") Tweet post) {
        List<Tag> tagList = tagExtractor.extract(post.getContent());
        post.setTags(tagList);
        try {
            repository.save(post);
            return ResultSuccess(true);
        } catch (Exception e) {
            return ResultFailure(e.getMessage());
        }
    }

    @Override
    public Result<List<Tweet>> getAllTweets(Pageable pageable) {
        return ResultSuccess(repository.findAll(pageable).getContent());
    }

    @Override
    public Result<List<Tweet>> getTweetsFromFollowingUsers(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return ResultSuccess(repository.findTweetsFromFollowingUsers(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getAllFromUserById(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return ResultSuccess(repository.findByOwnerId(userId, pageable));
        }
        return ResultFailure(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<List<Tweet>> getMostVotedTweets(int hours, Pageable pageable) {
        if (hours <= 0){
            return ResultFailure(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG);
        }
        return ResultSuccess(repository.findMostPopularByVotes(hours, pageable));
    }

    @Override
    public Result<List<Tweet>> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable) {
        return ResultSuccess(repository.findDistinctByTagsInOrderByCreateDateDesc(tagList, pageable));
    }

}
