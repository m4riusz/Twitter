package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.dao.UserDao;
import com.twitter.exception.TweetCreateException;
import com.twitter.exception.TweetNotFoundException;
import com.twitter.exception.TwitterGetException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.User;
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
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
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
    private UserDao userDao;

    @Mock
    private TweetDao tweetDao;

    private TweetService tweetService;

    @Before
    public void setUp() {
        tweetService = new TweetServiceImpl(tweetDao, userDao);
    }

    @Test(expected = TweetNotFoundException.class)
    public void getTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.getTweetById(1L);
    }

    @Test
    public void getTweetById_tweetExists() {
        when(tweetDao.exists(anyLong())).thenReturn(true);
        Tweet tweet = a(tweet().withOwner(a(user())));
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        Result<Tweet> tweetById = tweetService.getTweetById(TestUtil.ID_ONE);
        assertThat(tweetById, hasFinishedSuccessfully());
        assertThat(tweetById, hasValueOf(tweet));
    }

    @Test(expected = TweetCreateException.class)
    public void createTweet_userIsNull() {
        tweetService.createTweet(null, a(tweet()));
    }

    @Test(expected = TweetCreateException.class)
    public void createTweet_tweetIsNull() {
        tweetService.createTweet(a(user()), null);
    }

    @Test
    public void createTweet_saveTweet() {
        User user = a(user());
        Tweet tweet = a(tweet());
        when(tweetDao.save(tweet)).thenReturn(tweet);
        tweetService.createTweet(user, tweet);
    }

    @Test(expected = TweetNotFoundException.class)
    public void deleteTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.deleteTweetById(TestUtil.ID_ONE);
    }

    @Test
    public void deleteTweetById_tweetExists() {
        when(tweetDao.exists(anyLong())).thenReturn(true);
        Result<Boolean> deleteTweetById = tweetService.deleteTweetById(1L);
        assertThat(deleteTweetById, hasFinishedSuccessfully());
        assertThat(deleteTweetById, hasValueOf(Boolean.TRUE));
    }

    @Test
    public void getAllTweets_noTweets() {
        when(tweetDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(emptyList()));
        Result<List<Tweet>> allTweetsResult = tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allTweetsResult, hasFinishedSuccessfully());
        assertThat(allTweetsResult, hasValueOf(emptyList()));
    }

    @Test
    public void getAllTweets_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(tweetOne, tweetTwo)));
        Result<List<Tweet>> allTweetsResult = tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allTweetsResult, hasFinishedSuccessfully());
        assertThat(allTweetsResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
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
        assertThat(allTweetsPageTwo, hasFinishedSuccessfully());
        assertThat(allTweetsPageTwo, hasValueOf(aListWith(tweetThree)));
    }

    @Test(expected = UserNotFoundException.class)
    public void getTweetsFromFollowingUsers_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(Boolean.FALSE);
        tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getTweetsFromFollowingUsers_noTweetsFromFollowingUsers() {
        when(userDao.exists(anyLong())).thenReturn(Boolean.TRUE);
        when(tweetDao.findTweetsFromFollowingUsers(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<Tweet>> tweetsFromFollowingUsers = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromFollowingUsers, hasFinishedSuccessfully());
        assertThat(tweetsFromFollowingUsers, hasValueOf(emptyList()));
    }

    @Test
    public void getTweetsFromFollowingUsers_someTweetsFromFollowingUsers() {
        when(userDao.exists(anyLong())).thenReturn(Boolean.TRUE);
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findTweetsFromFollowingUsers(anyLong(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> tweetsFromFollowingUsers = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromFollowingUsers, hasFinishedSuccessfully());
        assertThat(tweetsFromFollowingUsers, hasValueOf(aListWith(tweetOne, tweetTwo)));
    }

    @Test(expected = UserNotFoundException.class)
    public void getTweetsFromUser_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(Boolean.FALSE);
        tweetService.getTweetsFromUser(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getTweetsFromUser_noTweets() {
        when(userDao.exists(anyLong())).thenReturn(Boolean.TRUE);
        when(tweetDao.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        Result<List<Tweet>> tweetsFromUserResult = tweetService.getTweetsFromUser(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromUserResult, hasFinishedSuccessfully());
        assertThat(tweetsFromUserResult, hasValueOf(emptyList()));
    }

    @Test
    public void getTweetsFromUser_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(userDao.exists(anyLong())).thenReturn(Boolean.TRUE);
        when(tweetDao.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> tweetsFromUserResult = tweetService.getTweetsFromUser(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromUserResult, hasFinishedSuccessfully());
        assertThat(tweetsFromUserResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
    }

    @Test
    public void getTweetsFromUser_pagingTest() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);
        when(userDao.exists(anyLong())).thenReturn(Boolean.TRUE);
        when(tweetDao.findByOwnerId(TestUtil.ID_ONE, pageOneRequest)).thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findByOwnerId(TestUtil.ID_ONE, pageTwoRequest)).thenReturn(aListWith(tweetThree));
        Result<List<Tweet>> allTweetsPageOne = tweetService.getTweetsFromUser(TestUtil.ID_ONE, pageOneRequest);
        Result<List<Tweet>> allTweetsPageTwo = tweetService.getTweetsFromUser(TestUtil.ID_ONE, pageTwoRequest);
        assertThat(allTweetsPageOne, hasFinishedSuccessfully());
        assertThat(allTweetsPageOne, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(allTweetsPageTwo, hasFinishedSuccessfully());
        assertThat(allTweetsPageTwo, hasValueOf(aListWith(tweetThree)));
    }

    @Test(expected = TwitterGetException.class)
    public void getMostVotedTweets_hoursSmallerThanZero() {
        tweetService.getMostVotedTweets(-1, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = TwitterGetException.class)
    public void getMostVotedTweets_hoursEqualsZero() {
        tweetService.getMostVotedTweets(0, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getMostVotedTweets_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findMostPopularByVotes(anyInt(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        Result<List<Tweet>> mostVotedTweetsResult = tweetService.getMostVotedTweets(10, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(mostVotedTweetsResult, hasFinishedSuccessfully());
        assertThat(mostVotedTweetsResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
    }

    @Test
    public void getMostVotedTweets_pagingTest() {
        int hours = 10;
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);
        when(userDao.exists(anyLong())).thenReturn(Boolean.TRUE);
        when(tweetDao.findMostPopularByVotes(hours, pageOneRequest)).thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findMostPopularByVotes(hours, pageTwoRequest)).thenReturn(aListWith(tweetThree));
        Result<List<Tweet>> mostVotedTweetsPageOneResult = tweetService.getMostVotedTweets(hours, pageOneRequest);
        Result<List<Tweet>> mostVotedTweetsPageTwoResult = tweetService.getMostVotedTweets(hours, pageTwoRequest);
        assertThat(mostVotedTweetsPageOneResult, hasFinishedSuccessfully());
        assertThat(mostVotedTweetsPageOneResult, hasValueOf(aListWith(tweetOne, tweetTwo)));
        assertThat(mostVotedTweetsPageTwoResult, hasFinishedSuccessfully());
        assertThat(mostVotedTweetsPageTwoResult, hasValueOf(aListWith(tweetThree)));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_noTags() {
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class))).thenReturn(emptyList());
        Result<List<Tweet>> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(emptyList(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestResult, hasValueOf(emptyList()));
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
        assertThat(tweetsByTagsOrderedByNewestPageTwoResult, hasFinishedSuccessfully());
        assertThat(tweetsByTagsOrderedByNewestPageTwoResult, hasValueOf(aListWith(tweetThree)));
    }
}
