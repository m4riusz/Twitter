package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.exception.TwitterGetException;
import com.twitter.exception.UserNotFoundException;
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

/**
 * Created by mariusz on 29.07.16.
 */
@Service
@Transactional
public class TweetServiceImpl extends PostServiceImpl<Tweet, TweetDao> implements TweetService {

    private final TagExtractor tagExtractor;

    @Autowired
    public TweetServiceImpl(TweetDao tweetDao, UserService userService, UserVoteService userVoteService, TagExtractor tagExtractor) {
        super(tweetDao, userService, userVoteService);
        this.tagExtractor = tagExtractor;
    }

    @Override
    @PreAuthorize(SecurityUtil.POST_PERSONAL)
    public Tweet create(@Param("post") Tweet post) {
        List<Tag> tagList = tagExtractor.extract(post.getContent());
        post.setTags(tagList);
        return super.create(post);
    }

    @Override
    public List<Tweet> getAllTweets(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public List<Tweet> getTweetsFromFollowingUsers(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return repository.findTweetsFromFollowingUsers(userId, pageable);
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Tweet> getAllFromUserById(long userId, Pageable pageable) {
        if (doesUserExist(userId)) {
            return repository.findByOwnerId(userId, pageable);
        }
        throw new UserNotFoundException(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG);
    }

    @Override
    public List<Tweet> getMostVotedTweets(int hours, Pageable pageable) {
        if (hours <= 0){
            throw new TwitterGetException(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG);
        }
        return repository.findMostPopularByVotes(hours, pageable);
    }

    @Override
    public List<Tweet> getTweetsByTagsOrderedByNewest(List<Tag> tagList, Pageable pageable) {
        return repository.findDistinctByTagsInOrderByCreateDateDesc(tagList, pageable);
    }

}
