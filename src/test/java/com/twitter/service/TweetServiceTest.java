package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import com.twitter.util.TagExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.matchers.ResultIsFailureMatcher.hasFailed;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultMessageMatcher.hasMessageOf;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 29.07.16.
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TweetServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TweetDao tweetDao;

    @Mock
    private TagExtractor tagExtractor;

    @Mock
    private UserVoteDao userVoteDao;

    private TweetService tweetService;

    @Before
    public void setUp() {
        tweetService = new TweetServiceImpl(tweetDao, userService, userVoteDao, tagExtractor);
    }

    public void getTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        Result<Tweet> tweetResult = tweetService.getById(1L);
        assertThat(tweetResult, hasFailed());
        assertThat(tweetResult, hasValueOf(null));
        assertThat(tweetResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getTweetById_tweetExists() {
        when(tweetDao.exists(anyLong())).thenReturn(true);
        Tweet tweet = a(tweet().withOwner(a(user())));
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        Result<Tweet> tweetById = tweetService.getById(TestUtil.ID_ONE);
        assertThat(tweetById, hasFinishedSuccessfully());
        assertThat(tweetById, hasValueOf(tweet));
        assertThat(tweetById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void createTweet_saveTweet() {
        Tweet tweet = a(tweet());
        when(tweetDao.save(tweet)).thenReturn(tweet);
        Result<Boolean> tweetResult = tweetService.create(tweet);
        assertThat(tweetResult, hasFinishedSuccessfully());
        assertThat(tweetResult, hasValueOf(true));
        assertThat(tweetResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }


    public void deleteTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        Result<Boolean> tweetResult = tweetService.delete(TestUtil.ID_ONE);
        assertThat(tweetResult, hasFailed());
        assertThat(tweetResult, hasValueOf(null));
        assertThat(tweetResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void deleteTweetById_tweetExists() {
        when(tweetDao.exists(anyLong())).thenReturn(true);
        Result<Boolean> deleteTweetById = tweetService.delete(1L);
        assertThat(deleteTweetById, hasFinishedSuccessfully());
        assertThat(deleteTweetById, hasValueOf(true));
        assertThat(deleteTweetById, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getAllTweets_noTweets() {
        when(tweetDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(emptyList()));
        Result<List<Tweet>> allTweetsResult = tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allTweetsResult, hasFinishedSuccessfully());
        assertThat(allTweetsResult, hasValueOf(emptyList()));
        assertThat(allTweetsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getAllTweets_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(tweetOne, tweetTwo)));
        Result<List<Tweet>> allTweetsResult = tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allTweetsResult, hasFinishedSuccessfully());
        assertThat(allTweetsResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(allTweetsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getAllTweets_pagingTest() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);
        when(tweetDao.findAll(pageOneRequest)).thenReturn(new PageImpl<>(aListWith(tweetOne, tweetTwo)));
        when(tweetDao.findAll(pageTwoRequest)).thenReturn(new PageImpl<>(aListWith(tweetThree)));
        Result<List<Tweet>> allTweetsPageOne = tweetService.getAllTweets(pageOneRequest);
        Result<List<Tweet>> allTweetsPageTwo = tweetService.getAllTweets(pageTwoRequest);
        assertThat(allTweetsPageOne, hasFinishedSuccessfully());
        assertThat(allTweetsPageOne, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(allTweetsPageOne, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
        assertThat(allTweetsPageTwo, hasFinishedSuccessfully());
        assertThat(allTweetsPageTwo, hasValueOf(aListWith(tweetThree)));
        assertThat(allTweetsPageTwo, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }


    public void getTweetsFromFollowingUsers_userDoesNotExist() {
        when(userService.exists(anyLong())).thenReturn(false);
        Result<List<Tweet>> tweetsResult = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsResult, hasFailed());
        assertThat(tweetsResult, hasValueOf(null));
        assertThat(tweetsResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getTweetsFromFollowingUsers_noTweetsFromFollowingUsers() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findTweetsFromFollowingUsers(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<Tweet>> tweetsFromFollowingUsers = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromFollowingUsers, hasFinishedSuccessfully());
        assertThat(tweetsFromFollowingUsers, hasValueOf(emptyList()));
        assertThat(tweetsFromFollowingUsers, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsFromFollowingUsers_someTweetsFromFollowingUsers() {
        when(userService.exists(anyLong())).thenReturn(true);
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findTweetsFromFollowingUsers(anyLong(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> tweetsFromFollowingUsers = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromFollowingUsers, hasFinishedSuccessfully());
        assertThat(tweetsFromFollowingUsers, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(tweetsFromFollowingUsers, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    public void getTweetsFromUser_userDoesNotExist() {
        when(userService.exists(anyLong())).thenReturn(false);
        Result<List<Tweet>> tweetResult = tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetResult, hasFailed());
        assertThat(tweetResult, hasValueOf(null));
        assertThat(tweetResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getTweetsFromUser_noTweets() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<Tweet>> tweetsFromUserResult = tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromUserResult, hasFinishedSuccessfully());
        assertThat(tweetsFromUserResult, hasValueOf(emptyList()));
        assertThat(tweetsFromUserResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsFromUser_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> tweetsFromUserResult = tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromUserResult, hasFinishedSuccessfully());
        assertThat(tweetsFromUserResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(tweetsFromUserResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsFromUser_pagingTest() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findByOwnerId(TestUtil.ID_ONE, pageOneRequest)).thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findByOwnerId(TestUtil.ID_ONE, pageTwoRequest)).thenReturn(aListWith(tweetThree));
        Result<List<Tweet>> allTweetsPageOne = tweetService.getAllFromUserById(TestUtil.ID_ONE, pageOneRequest);
        Result<List<Tweet>> allTweetsPageTwo = tweetService.getAllFromUserById(TestUtil.ID_ONE, pageTwoRequest);
        assertThat(allTweetsPageOne, hasFinishedSuccessfully());
        assertThat(allTweetsPageOne, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(allTweetsPageOne, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
        assertThat(allTweetsPageTwo, hasFinishedSuccessfully());
        assertThat(allTweetsPageTwo, hasValueOf(aListWith(tweetThree)));
        assertThat(allTweetsPageTwo, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }


    public void getMostVotedTweets_hoursSmallerThanZero() {
        Result<List<Tweet>> tweetResult = tweetService.getMostVotedTweets(-1, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetResult, hasFailed());
        assertThat(tweetResult, hasValueOf(null));
        assertThat(tweetResult, hasMessageOf(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG));
    }

    public void getMostVotedTweets_hoursEqualsZero() {
        Result<List<Tweet>> tweetResult = tweetService.getMostVotedTweets(0, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetResult, hasFailed());
        assertThat(tweetResult, hasValueOf(null));
        assertThat(tweetResult, hasMessageOf(MessageUtil.HOURS_CANT_BE_LESS_OR_EQUAL_0_ERROR_MSG));
    }

    @Test
    public void getMostVotedTweets_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findMostPopularByVotes(anyInt(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> mostVotedTweetsResult = tweetService.getMostVotedTweets(10, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(mostVotedTweetsResult, hasFinishedSuccessfully());
        assertThat(mostVotedTweetsResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(mostVotedTweetsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getMostVotedTweets_pagingTest() {
        int hours = 10;
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findMostPopularByVotes(hours, pageOneRequest)).thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findMostPopularByVotes(hours, pageTwoRequest)).thenReturn(aListWith(tweetThree));
        Result<List<Tweet>> mostVotedTweetsPageOneResult = tweetService.getMostVotedTweets(hours, pageOneRequest);
        Result<List<Tweet>> mostVotedTweetsPageTwoResult = tweetService.getMostVotedTweets(hours, pageTwoRequest);
        assertThat(mostVotedTweetsPageOneResult, hasFinishedSuccessfully());
        assertThat(mostVotedTweetsPageOneResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(mostVotedTweetsPageOneResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
        assertThat(mostVotedTweetsPageTwoResult, hasFinishedSuccessfully());
        assertThat(mostVotedTweetsPageTwoResult, hasValueOf(aListWith(tweetThree)));
        assertThat(mostVotedTweetsPageTwoResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_noTags() {
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class))).thenReturn(emptyList());
        Result<List<Tweet>> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(emptyList(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestResult, hasValueOf(emptyList()));
        assertThat(tweetsByTagsOrderedByNewestResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_oneTag() {
        Tag tag = a(tag());
        Tweet tweetOne = a(tweet().withTags(aListWith(tag)));
        Tweet tweetTwo = a(tweet().withTags(aListWith(tag)));
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class)))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tag), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(tweetsByTagsOrderedByNewestResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_someTags() {
        Tag tagOne = a(tag());
        Tag tagTwo = a(tag());
        Tweet tweetOne = a(tweet().withTags(aListWith(tagOne)));
        Tweet tweetTwo = a(tweet().withTags(aListWith(tagTwo)));
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class)))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne, tagTwo), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(tweetsByTagsOrderedByNewestResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_pagingTest() {
        Tag tagOne = a(tag());
        User owner = a(user());
        Tweet tweetOne = a(tweet().withOwner(owner).withTags(aListWith(tagOne)));
        Tweet tweetTwo = a(tweet().withOwner(owner).withTags(aListWith(tagOne)));
        Tweet tweetThree = a(tweet().withOwner(owner).withTags(aListWith(tagOne)));
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);

        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tagOne), pageOneRequest))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tagOne), pageTwoRequest))
                .thenReturn(aListWith(tweetThree));
        Result<List<Tweet>> tweetsByTagsOrderedByNewestPageOneResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne), pageOneRequest);
        Result<List<Tweet>> tweetsByTagsOrderedByNewestPageTwoResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne), pageTwoRequest);
        assertThat(tweetsByTagsOrderedByNewestPageOneResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestPageOneResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(tweetsByTagsOrderedByNewestPageOneResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
        assertThat(tweetsByTagsOrderedByNewestPageTwoResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestPageTwoResult, hasValueOf(aListWith(tweetThree)));
        assertThat(tweetsByTagsOrderedByNewestPageTwoResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void vote_userDoesNotExist() {
        when(userService.exists(anyLong())).thenReturn(false);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(tweet())
                )
        );
        Result<Boolean> voteResult = tweetService.vote(userVote);
        assertThat(voteResult, hasFailed());
        assertThat(voteResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void vote_userExistsPostDoesNot() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.exists(anyLong())).thenReturn(false);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(tweet())
                )
        );
        Result<Boolean> voteResult = tweetService.vote(userVote);
        assertThat(voteResult, hasFailed());
        assertThat(voteResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void vote_userAndPostExistsButPostAlreadyVoted() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(tweet())
                )
        );
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(userVote);
        Result<Boolean> voteResult = tweetService.vote(userVote);
        assertThat(voteResult, hasFailed());
        assertThat(voteResult, hasMessageOf(MessageUtil.POST_ALREADY_VOTED));
    }

    @Test
    public void vote_successVote() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(null);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(tweet())
                )
        );
        Result<Boolean> voteResult = tweetService.vote(userVote);
        assertThat(voteResult, hasFinishedSuccessfully());
        assertThat(voteResult, hasValueOf(true));
        assertThat(voteResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

}
