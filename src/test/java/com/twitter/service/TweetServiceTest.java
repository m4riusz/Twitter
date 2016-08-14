package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.TweetDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.exception.PostDeleteException;
import com.twitter.exception.PostNotFoundException;
import com.twitter.exception.TwitterGetException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import com.twitter.util.TagExtractor;
import com.twitter.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.twitter.builders.PostVoteBuilder.postVote;
import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 29.07.16.
 */

@SpringBootTest
@ActiveProfiles(Profiles.DEV)
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

    @Test(expected = PostNotFoundException.class)
    public void getTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.getById(TestUtil.ID_ONE);
    }

    @Test
    public void getTweetById_tweetExists() {
        Tweet tweet = a(tweet().withOwner(a(user())));
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        Tweet tweetFromDb = tweetService.getById(TestUtil.ID_ONE);
        assertThat(tweetFromDb, is(tweet));
    }

    @Test
    public void createTweet_saveTweet() {
        Tweet tweet = a(tweet());
        when(tweetDao.save(tweet)).thenReturn(tweet);
        Tweet savedTweet = tweetService.create(tweet);
        assertThat(savedTweet, is(tweet));
    }

    @Test(expected = PostNotFoundException.class)
    public void deleteTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.delete(TestUtil.ID_ONE);
    }

    @Test(expected = PostDeleteException.class)
    public void deleteTweetById_tweetExistsUserIsNotPostOwner() {
        User user = a(user());
        User otherUser = a(user());
        Tweet tweet = a(tweet()
                .withOwner(user)
        );
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(otherUser);
        tweetService.delete(tweet.getId());
    }

    @Test
    public void deleteTweetById_tweetExistsUserIsPostOwner() {
        User user = a(user());
        Tweet tweet = a(tweet()
                .withOwner(user)
        );
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        tweetService.delete(tweet.getId());
        assertThat(tweet.getContent(), is(MessageUtil.DELETE_BY_OWNED_ABSTRACT_POST_CONTENT));
        assertThat(tweet.isBanned(), is(true));
    }

    @Test(expected = PostDeleteException.class)
    public void deleteCommentById_commentAlreadyDeleted() {
        User user = a(user());
        Tweet tweet = a(tweet()
                .withOwner(user)
                .withBanned(true)
        );
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        tweetService.delete(tweet.getId());
    }

    @Test
    public void getAllTweets_noTweets() {
        when(tweetDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(emptyList()));
        List<Tweet> allTweetsResult = tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allTweetsResult, is(emptyList()));
    }

    @Test
    public void getAllTweets_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(aListWith(tweetOne, tweetTwo)));
        List<Tweet> allTweetsResult = tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(allTweetsResult, is(aListWith(tweetOne, tweetTwo)));
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
        List<Tweet> allTweetsPageOne = tweetService.getAllTweets(pageOneRequest);
        List<Tweet> allTweetsPageTwo = tweetService.getAllTweets(pageTwoRequest);
        assertThat(allTweetsPageOne, contains(tweetOne, tweetTwo));
        assertThat(allTweetsPageTwo, contains(tweetThree));
    }

    @Test(expected = UserNotFoundException.class)
    public void getTweetsFromFollowingUsers_userDoesNotExist() {
        when(userService.exists(anyLong())).thenReturn(false);
        tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getTweetsFromFollowingUsers_noTweetsFromFollowingUsers() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findTweetsFromFollowingUsers(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        List<Tweet> tweetsFromFollowingUsers = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromFollowingUsers, is(emptyList()));
    }

    @Test
    public void getTweetsFromFollowingUsers_someTweetsFromFollowingUsers() {
        when(userService.exists(anyLong())).thenReturn(true);
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(tweetDao.findTweetsFromFollowingUsers(anyLong(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsFromFollowingUsers = tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromFollowingUsers, hasItems(tweetOne, tweetTwo));
    }

    @Test(expected = UserNotFoundException.class)
    public void getTweetsFromUser_userDoesNotExist() {
        when(userService.exists(anyLong())).thenReturn(false);
        tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getTweetsFromUser_noTweets() {
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(emptyList());
        List<Tweet> tweetsFromUserResult = tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromUserResult, is(emptyList()));
    }

    @Test
    public void getTweetsFromUser_someTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsFromUserResult = tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsFromUserResult, hasItems(tweetOne, tweetTwo));
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
        List<Tweet> allTweetsPageOne = tweetService.getAllFromUserById(TestUtil.ID_ONE, pageOneRequest);
        List<Tweet> allTweetsPageTwo = tweetService.getAllFromUserById(TestUtil.ID_ONE, pageTwoRequest);
        assertThat(allTweetsPageOne, hasItems(tweetOne, tweetTwo));
        assertThat(allTweetsPageTwo, hasItems(tweetThree));
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
        List<Tweet> mostVotedTweetsResult = tweetService.getMostVotedTweets(10, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(mostVotedTweetsResult, hasItems(tweetOne, tweetTwo));
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
        List<Tweet> mostVotedTweetsPageOneResult = tweetService.getMostVotedTweets(hours, pageOneRequest);
        List<Tweet> mostVotedTweetsPageTwoResult = tweetService.getMostVotedTweets(hours, pageTwoRequest);
        assertThat(mostVotedTweetsPageOneResult, contains(tweetOne, tweetTwo));
        assertThat(mostVotedTweetsPageTwoResult, contains(tweetThree));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_noTags() {
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class))).thenReturn(emptyList());
        List<Tweet> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(emptyList(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, is(emptyList()));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_oneTag() {
        Tag tag = a(tag());
        Tweet tweetOne = a(tweet().withTags(aListWith(tag)));
        Tweet tweetTwo = a(tweet().withTags(aListWith(tag)));
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class)))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tag), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_someTags() {
        Tag tagOne = a(tag());
        Tag tagTwo = a(tag());
        Tweet tweetOne = a(tweet().withTags(aListWith(tagOne)));
        Tweet tweetTwo = a(tweet().withTags(aListWith(tagTwo)));
        when(tweetDao.findDistinctByTagsInOrderByCreateDateDesc(anyListOf(Tag.class), any(Pageable.class)))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne, tagTwo), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, is(aListWith(tweetOne, tweetTwo)));
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
        List<Tweet> tweetsByTagsOrderedByNewestPageOneResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne), pageOneRequest);
        List<Tweet> tweetsByTagsOrderedByNewestPageTwoResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne), pageTwoRequest);
        assertThat(tweetsByTagsOrderedByNewestPageOneResult, is(aListWith(tweetOne, tweetTwo)));
        assertThat(tweetsByTagsOrderedByNewestPageTwoResult, is(aListWith(tweetThree)));
    }

    @Test(expected = UserNotFoundException.class)
    public void vote_userDoesNotExist() {
        when(userService.getCurrentLoggedUser()).thenThrow(UserNotFoundException.class);
        tweetService.vote(a(postVote()));
    }

    @Test(expected = PostNotFoundException.class)
    public void vote_userExistsPostDoesNot() {
        when(userService.getCurrentLoggedUser()).thenReturn(a(user()));
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.vote(a(postVote()));
    }

    @Test
    public void vote_successVoteCreate() {
        User user = a(user());
        Tweet tweet = a(tweet());
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(null);
        UserVote userVote = tweetService.vote(
                a(postVote()
                        .withPostId(tweet.getId())
                        .withVote(Vote.UP)
                )
        );
        assertThat(userVote.getVote(), is(Vote.UP));
        assertThat(userVote.getAbstractPost(), is(tweet));
        assertThat(userVote.getUser(), is(user));
    }

    @Test
    public void vote_successVoteChange() {
        User user = a(user());
        Tweet tweet = a(tweet());
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(user);

        UserVote vote = a(userVote()
                .withUser(user)
                .withAbstractPost(tweet)
                .withVote(Vote.UP)
        );
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(vote);
        UserVote userVote = tweetService.vote(
                a(postVote()
                        .withPostId(tweet.getId())
                        .withVote(Vote.DOWN)
                )
        );

        assertThat(userVote.getVote(), is(Vote.DOWN));
        assertThat(userVote.getAbstractPost(), is(tweet));
        assertThat(userVote.getUser(), is(user));
    }
}
