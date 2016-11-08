package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.TweetDao;
import com.twitter.dto.PostVote;
import com.twitter.exception.*;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import com.twitter.util.TestUtil;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static com.twitter.builders.PostVoteBuilder.postVote;
import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by mariusz on 29.07.16.
 */

@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@PrepareForTest({DateTime.class})
@RunWith(PowerMockRunner.class)
public class TweetServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TweetDao tweetDao;

    @Mock
    private UserVoteService userVoteService;

    @Mock
    private TagService tagService;

    private TweetService tweetService;

    @Before
    public void setUp() {
        tweetService = new TweetServiceImpl(tweetDao, userService, userVoteService, tagService);
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
                .withDeleted(false)
        );
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        tweetService.delete(tweet.getId());
        assertThat(tweet.getContent(), is(MessageUtil.DELETE_BY_OWNED_ABSTRACT_POST_CONTENT));
        assertThat(tweet.isDeleted(), is(true));
    }

    @Test(expected = PostDeleteException.class)
    public void deleteCommentById_commentAlreadyDeleted() {
        User user = a(user());
        Tweet tweet = a(tweet()
                .withOwner(user)
                .withDeleted(true)
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
        Date date = TestUtil.DATE_2000;
        User owner = a(user());
        Date createDate = TestUtil.DATE_2000;
        Tweet tweetOne = a(tweet().withOwner(owner).withCreateDate(createDate));
        Tweet tweetTwo = a(tweet().withOwner(owner).withCreateDate(createDate));
        when(tweetDao.findByCreateDateAfterOrderByVotesVoteAscCreateDateDesc(any(Date.class), any(Pageable.class))).thenReturn(aListWith(tweetOne, tweetTwo));
        mockStatic(DateTime.class);
        PowerMockito.when(DateTime.now()).thenReturn(new DateTime(date));
        List<Tweet> mostVotedTweetsResult = tweetService.getMostVotedTweets(10, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(mostVotedTweetsResult, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void getMostVotedTweets_pagingTest() {
        Date date = TestUtil.DATE_2000;
        int hours = 10;
        User tweetOwner = a(user());
        Tweet tweetOne = a(tweet().withOwner(tweetOwner));
        Tweet tweetTwo = a(tweet().withOwner(tweetOwner));
        Tweet tweetThree = a(tweet().withOwner(tweetOwner));
        Pageable pageOneRequest = new PageRequest(0, 2);
        Pageable pageTwoRequest = new PageRequest(1, 2);
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findByCreateDateAfterOrderByVotesVoteAscCreateDateDesc(new DateTime(date).minusHours(hours).toDate(), pageOneRequest)).thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findByCreateDateAfterOrderByVotesVoteAscCreateDateDesc(new DateTime(date).minusHours(hours).toDate(), pageTwoRequest)).thenReturn(aListWith(tweetThree));
        mockStatic(DateTime.class);
        PowerMockito.when(DateTime.now()).thenReturn(new DateTime(date));
        List<Tweet> mostVotedTweetsPageOneResult = tweetService.getMostVotedTweets(hours, pageOneRequest);
        List<Tweet> mostVotedTweetsPageTwoResult = tweetService.getMostVotedTweets(hours, pageTwoRequest);
        assertThat(mostVotedTweetsPageOneResult, contains(tweetOne, tweetTwo));
        assertThat(mostVotedTweetsPageTwoResult, contains(tweetThree));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_noTags() {
        when(tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(anyListOf(String.class), any(Pageable.class))).thenReturn(emptyList());
        List<Tweet> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(emptyList(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, is(emptyList()));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_oneTag() {
        Tag tag = a(tag());
        Tweet tweetOne = a(tweet().withTags(aListWith(tag)));
        Tweet tweetTwo = a(tweet().withTags(aListWith(tag)));
        when(tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(anyListOf(String.class), any(Pageable.class)))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tag.getText()), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tweetsByTagsOrderedByNewestResult, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void getTweetsByTagsOrderedByNewest_someTags() {
        Tag tagOne = a(tag());
        Tag tagTwo = a(tag());
        Tweet tweetOne = a(tweet().withTags(aListWith(tagOne)));
        Tweet tweetTwo = a(tweet().withTags(aListWith(tagTwo)));
        when(tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(anyListOf(String.class), any(Pageable.class)))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsByTagsOrderedByNewestResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne.getText(), tagTwo.getText()), TestUtil.ALL_IN_ONE_PAGE);
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

        when(tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(aListWith(tagOne.getText()), pageOneRequest))
                .thenReturn(aListWith(tweetOne, tweetTwo));
        when(tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(aListWith(tagOne.getText()), pageTwoRequest))
                .thenReturn(aListWith(tweetThree));
        List<Tweet> tweetsByTagsOrderedByNewestPageOneResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne.getText()), pageOneRequest);
        List<Tweet> tweetsByTagsOrderedByNewestPageTwoResult = tweetService.getTweetsByTagsOrderedByNewest(aListWith(tagOne.getText()), pageTwoRequest);
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
        PostVote postVote = a(postVote()
                .withPostId(tweet.getId())
                .withVote(Vote.UP)
        );
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(userVoteService.findUserVoteForPost(any(User.class), any(AbstractPost.class))).thenReturn(null);
        when(userVoteService.save(any(UserVote.class))).thenReturn(new UserVote(postVote.getVote(), user, tweet));
        UserVote userVote = tweetService.vote(
                postVote
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
        when(userVoteService.findUserVoteForPost(any(User.class), any(AbstractPost.class))).thenReturn(vote);
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

    @Test(expected = UserNotFoundException.class)
    public void getFavouriteTweetsFromUser_userDoesNotExist() {
        when(userService.exists(anyLong())).thenReturn(false);
        tweetService.getFavouriteTweetsFromUser(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void getFavouriteTweetsFromUser_userExistSomeTweets() {
        User user = a(user());
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        when(userService.exists(anyLong())).thenReturn(true);
        when(tweetDao.findFavouriteTweetsFromUser(anyLong(), any(Pageable.class))).thenReturn(
                aListWith(
                        tweetOne,
                        tweetTwo,
                        tweetThree
                )
        );
        List<Tweet> favouriteTweetsFromUser = tweetService.getFavouriteTweetsFromUser(user.getId(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(favouriteTweetsFromUser, hasItems(tweetOne, tweetTwo, tweetThree));
        assertThat(favouriteTweetsFromUser, hasSize(3));
    }

    @Test(expected = PostException.class)
    public void addTweetToFavourites_tweetAlreadyInFavouritesTweets() {
        Tweet tweet = a(tweet());
        User user = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweet
                        )
                )
        );
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(tweetDao.doesTweetBelongToUserFavouritesTweets(anyLong(), anyLong())).thenReturn(true);
        tweetService.addTweetToFavourites(tweet.getId());
    }

    @Test
    public void addTweetToFavourites_tweetIsNotInUserFavouritesTweets() {
        Tweet tweet = a(tweet());
        User user = a(user());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(tweetDao.doesTweetBelongToUserFavouritesTweets(anyLong(), anyLong())).thenReturn(false);

        Tweet tweetFromDb = tweetService.addTweetToFavourites(tweet.getId());
        assertThat(tweetFromDb, is(tweet));
        assertThat(user.getFavouriteTweets(), hasItem(tweet));
    }

    @Test(expected = PostException.class)
    public void deleteTweetFromFavourites_tweetIsNotInFavouritesTweets() {
        Tweet tweet = a(tweet());
        User user = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweet
                        )
                )
        );
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(tweetDao.doesTweetBelongToUserFavouritesTweets(anyLong(), anyLong())).thenReturn(false);
        tweetService.deleteTweetFromFavourites(tweet.getId());
    }

    @Test
    public void deleteTweetFromFavourites_tweetIsInUserFavouritesTweets() {
        Tweet tweetOne = a(tweet());
        Tweet tweetTwo = a(tweet());
        Tweet tweetThree = a(tweet());
        User user = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweetOne,
                                tweetTwo,
                                tweetThree
                        )
                )
        );
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweetOne);
        when(tweetDao.doesTweetBelongToUserFavouritesTweets(anyLong(), anyLong())).thenReturn(true);

        tweetService.deleteTweetFromFavourites(tweetOne.getId());
        assertThat(user.getFavouriteTweets(), not(hasItem(tweetOne)));
        assertThat(user.getFavouriteTweets(), hasItems(tweetTwo, tweetThree));
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteVote_userDoesNotExist() {
        when(userService.getCurrentLoggedUser()).thenThrow(UserNotFoundException.class);
        tweetService.deleteVote(TestUtil.ID_ONE);
    }

    @Test(expected = PostNotFoundException.class)
    public void deleteVote_userExistsPostDoesNot() {
        when(userService.getCurrentLoggedUser()).thenReturn(a(user()));
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.deleteVote(TestUtil.ID_ONE);
    }

    @Test
    public void deleteVote_successDeleteVote() {
        User owner = a(user());
        Tweet tweet = a(tweet());
        UserVote userVote = a(userVote()
                .withVote(Vote.UP)
                .withUser(owner)
                .withAbstractPost(tweet)
        );
        tweet.setVotes(aListWith(userVote));
        tweet.setOwner(owner);

        when(userService.getCurrentLoggedUser()).thenReturn(owner);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        tweetService.deleteVote(userVote.getId());

        assertThat(tweet.getVotes(), not(hasItem(userVote)));
    }

    @Test(expected = PostNotFoundException.class)
    public void tweetBelongsToFavouriteTweets_tweetDoesNotExist() {
        tweetService.tweetBelongsToFavouriteTweets(TestUtil.ID_ONE);
    }

    @Test
    public void tweetBelongsToFavouriteTweets_tweetNotInFavourites() {
        User user = a(user());
        Tweet tweet = a(tweet());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(tweetDao.doesTweetBelongToUserFavouritesTweets(anyLong(), anyLong())).thenReturn(false);
        boolean belong = tweetService.tweetBelongsToFavouriteTweets(TestUtil.ID_ONE);
        assertThat(belong, is(false));
    }

    @Test
    public void tweetBelongsToFavouriteTweets_tweetInFavourites() {
        User user = a(user());
        Tweet tweet = a(tweet());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(tweetDao.exists(anyLong())).thenReturn(true);
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        when(tweetDao.doesTweetBelongToUserFavouritesTweets(anyLong(), anyLong())).thenReturn(true);
        boolean belong = tweetService.tweetBelongsToFavouriteTweets(TestUtil.ID_ONE);
        assertThat(belong, is(true));
    }

    @Test
    public void getPostVote_postDoesNotExist() {
        User user = a(user());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(tweetDao.findOne(anyLong())).thenReturn(null);
        UserVote userVoteForPost = userVoteService.findUserVoteForPost(user, a(tweet()));
        assertThat(userVoteForPost, is(nullValue()));
    }

    @Test
    public void getPostVoteCount_postDoesNotExist() {
        long userVoteForPost = userVoteService.getPostVoteCount(TestUtil.ID_ONE, Vote.UP);
        assertThat(userVoteForPost, is(0L));
    }
}
