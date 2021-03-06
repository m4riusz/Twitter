package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.*;
import com.twitter.util.TestUtil;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 19.07.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TweetDaoTest {

    @Autowired
    private TweetDao tweetDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserVoteDao userVoteDao;

    @Test
    public void findAllTweetsByOwnerId_noTweets() {
        User user = a(user());
        userDao.save(user);
        List<Tweet> tweetsFromUser = tweetDao.findByOwnerId(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsFromUser, is(emptyList()));
    }

    @Test
    public void findAllTweetsByOwnerId_someTweets() {
        User user = a(user());
        userDao.save(user);
        Tweet tweet1 = a(tweet().withOwner(user));
        Tweet tweet2 = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweet1, tweet2));
        List<Tweet> tweetsFromUser = tweetDao.findByOwnerId(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsFromUser, hasItems(tweet1, tweet2));
    }

    @Test
    public void findAllTweetsByOwnerId_someTweetsAndSomeUsers() {
        User userOne = a(user());
        User userTwo = a(user());
        User userThree = a(user());

        userDao.save(aListWith(userOne, userTwo, userThree));
        Tweet tweet1 = a(tweet().withOwner(userOne));
        Tweet tweet2 = a(tweet().withOwner(userOne));
        Tweet tweet3 = a(tweet().withOwner(userOne));

        Tweet tweet4 = a(tweet().withOwner(userTwo));
        Tweet tweet5 = a(tweet().withOwner(userTwo));

        tweetDao.save(aListWith(tweet1, tweet2, tweet3, tweet4, tweet5));

        List<Tweet> tweetsFromUserOne = tweetDao.findByOwnerId(
                userOne.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        List<Tweet> tweetsFromUserTwo = tweetDao.findByOwnerId(
                userTwo.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        List<Tweet> tweetsFromUserThree = tweetDao.findByOwnerId(
                userThree.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsFromUserOne, hasItems(tweet1, tweet2, tweet3));
        assertThat(tweetsFromUserTwo, hasItems(tweet4, tweet5));
        assertThat(tweetsFromUserThree, is(emptyList()));
    }

    @Test
    public void findMostPopularAfterDateOrderByVotes_noTweets() {
        List<Tweet> mostPopular = tweetDao.findMostPopularAfterDateOrderByVotes(
                TestUtil.DATE_2000,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(mostPopular, is(emptyList()));
    }

    @Test
    public void findMostPopularAfterDateOrderByVotes_tooOldTweet() {
        User user = a(user());
        userDao.save(aListWith(user));
        Date tooOldDate = new DateTime(TestUtil.DATE_2003).minusMinutes(1).toDate();
        Tweet tweet = a(tweet().withOwner(user).withCreateDate(tooOldDate));
        tweetDao.save(aListWith(tweet));
        List<Tweet> mostPopularTweetList = tweetDao.findMostPopularAfterDateOrderByVotes(
                TestUtil.DATE_2003,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(mostPopularTweetList, is(emptyList()));
    }

    @Test
    public void findMostPopularAfterDateOrderByVotes_newTweet() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet()
                .withOwner(user)
                .withCreateDate(TestUtil.DATE_2001)
        );
        tweetDao.save(aListWith(tweet));
        List<Tweet> mostPopularTweetList = tweetDao.findMostPopularAfterDateOrderByVotes(
                TestUtil.DATE_2000,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(mostPopularTweetList, hasItem(tweet));
    }

    @Test
    public void findMostPopularAfterDateOrderByVotes_someOldAndNewTweets() {
        User user = a(user());

        userDao.save(aListWith(user));
        Date tooOldDate = DateTime.now().minusMinutes(10).toDate();
        Date currentDate = DateTime.now().toDate();
        Date currentDateTwo = DateTime.now().minusMinutes(1).toDate();
        Tweet tweetOne = a(tweet()
                .withOwner(user)
                .withCreateDate(tooOldDate)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(user)
                .withCreateDate(currentDate)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(user)
                .withCreateDate(currentDate)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<Tweet> mostPopularTweets = tweetDao.findMostPopularAfterDateOrderByVotes(
                currentDateTwo,
                new PageRequest(0, 2)
        );
        assertThat(mostPopularTweets, hasItems(tweetTwo, tweetThree));
        assertThat(mostPopularTweets, not(hasItem(tweetOne)));
    }

    @Test
    public void findMostPopularAfterDateOrderByVotes_unpopularAndPopularTweets() {
        User user = a(user());
        User user1 = a(user());
        User user2 = a(user());
        User user3 = a(user());

        userDao.save(aListWith(user, user1, user2, user3));
        Tweet tweetOne = a(tweet().withOwner(user));
        Tweet tweetTwo = a(tweet().withOwner(user));
        Tweet tweetThree = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        UserVote voteTweetOneUserOne = a(userVote().withUser(user1).withVote(Vote.UP).withAbstractPost(tweetOne));
        UserVote voteTweetOneUserTwo = a(userVote().withUser(user2).withVote(Vote.UP).withAbstractPost(tweetOne));
        UserVote voteTweetTwoUserOne = a(userVote().withUser(user1).withVote(Vote.UP).withAbstractPost(tweetTwo));
        UserVote voteTweetTwoUserTwo = a(userVote().withUser(user2).withVote(Vote.UP).withAbstractPost(tweetTwo));
        UserVote voteTweetTwoUserThree = a(userVote().withUser(user3).withVote(Vote.UP).withAbstractPost(tweetTwo));
        UserVote voteTweetThreeUserOne = a(userVote().withUser(user1).withVote(Vote.UP).withAbstractPost(tweetThree));

        userVoteDao.save(
                aListWith(
                        voteTweetOneUserOne,
                        voteTweetOneUserTwo,
                        voteTweetTwoUserOne,
                        voteTweetTwoUserTwo,
                        voteTweetTwoUserThree,
                        voteTweetThreeUserOne
                )
        );

        List<Tweet> mostPopularOnFirstPage = tweetDao.findMostPopularAfterDateOrderByVotes(
                DateTime.now().minusHours(1).toDate(),
                new PageRequest(0, 2)
        );
        List<Tweet> mostPopularSecondPage = tweetDao.findMostPopularAfterDateOrderByVotes(
                DateTime.now().minusHours(1).toDate(),
                new PageRequest(1, 2)
        );

        assertThat(mostPopularOnFirstPage, contains(tweetTwo, tweetOne));
        assertThat(mostPopularSecondPage, contains(tweetThree));
    }

    @Test
    public void findDistinctByTagsTextInOrderByCreateDateDesc_noTags() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet()
                .withOwner(user)
                .withTags(emptyList())
        );
        tweetDao.save(aListWith(tweet));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                emptyList(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsWithTag, is(emptyList()));
    }

    @Test
    public void findDistinctByTagsTextInOrderByCreateDateDesc_oneTag() {
        Tag tag = a(tag().withText("tag"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tag))
        );
        tweetDao.save(aListWith(tweet));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                aListWith(tag.getText()),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsWithTag, hasItem(tweet));
    }


    @Test
    public void findDistinctByTagsTextInOrderByCreateDateDesc_oneTweetWithSomeTagsFindUsingAllTags() {
        Tag tagOne = a(tag().withText("tag1"));
        Tag tagTwo = a(tag().withText("tag2"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet()
                .withOwner(user)
                .withTags(
                        aListWith(
                                tagOne,
                                tagTwo
                        ))
        );
        tweetDao.save(aListWith(tweet));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                aListWith(
                        tagOne.getText(),
                        tagTwo.getText()
                ),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsWithTag, hasItem(tweet));
        assertThat(tweetsWithTag, hasSize(1));
    }

    @Test
    public void findDistinctByTagsTextInOrderByCreateDateDesc_oneTagAndManyTweets() {
        Tag tag = a(tag().withText("tag1"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweetOne = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tag))
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tag))
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo));

        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                aListWith(tag.getText()),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsWithTag, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void findDistinctByTagsTextInOrderByCreateDateDesc_tweetOrderedByNewest() {
        Tag tag = a(tag().withText("tag"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweetOne = a(tweet()
                .withCreateDate(TestUtil.DATE_2003)
                .withOwner(user)
                .withTags(aListWith(tag))
        );
        Tweet tweetTwo = a(tweet()
                .withCreateDate(TestUtil.DATE_2002)
                .withOwner(user)
                .withTags(aListWith(tag))
        );
        Tweet tweetThree = a(tweet()
                .withCreateDate(TestUtil.DATE_2001)
                .withOwner(user)
                .withTags(aListWith(tag))
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));
        List<Tweet> firstPageTweets = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                aListWith(tag.getText()),
                new PageRequest(0, 2)
        );
        List<Tweet> secondPageTweets = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                aListWith(tag.getText()),
                new PageRequest(1, 2)
        );
        assertThat(firstPageTweets, contains(tweetOne, tweetTwo));
        assertThat(secondPageTweets, contains(tweetThree));
    }

    @Test
    public void findDistinctByTagsTextInOrderByCreateDateDesc_manyTweetsWithDifferentTagsFindAllByTags() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tag tagOne = a(tag().withText("tag1"));
        Tag tagTwo = a(tag().withText("tag2"));
        Tag tagThree = a(tag().withText("tag3"));
        Tweet tweetOne = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagTwo))
        );
        Tweet tweetThree = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagThree))
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));
        List<Tweet> tweetsWithTags = tweetDao.findDistinctByTagsTextInOrderByCreateDateDesc(
                aListWith(
                        tagOne.getText(),
                        tagTwo.getText(),
                        tagThree.getText()
                ),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsWithTags, hasItems(tweetOne, tweetTwo, tweetThree));
        assertThat(tweetsWithTags, hasSize(3));
    }

    @Test
    public void findTweetsFromFollowingUsers_noFollowingUsers() {
        User user = a(user());
        userDao.save(user);
        List<Tweet> tweetsFromFollowingUsers = tweetDao.findTweetsFromFollowingUsers(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsFromFollowingUsers, is(emptyList()));
    }

    @Test
    public void findTweetsFromFollowingUsers_oneFollowingAndSomeTweets() {
        User user = a(user());
        User followingOne = a(user().withFollowers(aListWith(user)));
        userDao.save(aListWith(user, followingOne));

        Tweet tweetOne = a(tweet()
                .withOwner(followingOne)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(followingOne)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo));

        List<Tweet> tweetsFromFollowingUsers = tweetDao.findTweetsFromFollowingUsers(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsFromFollowingUsers, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void findTweetsFromFollowingUsers_someFollowingAndSomeTweets() {
        User user = a(user());
        User followingOne = a(user().withFollowers(aListWith(user)));
        User followingTwo = a(user().withFollowers(aListWith(user)));
        userDao.save(aListWith(user, followingOne, followingTwo));

        Tweet tweetOne = a(tweet()
                .withOwner(followingOne)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(followingOne)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(followingTwo)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<Tweet> tweetsFromFollowingUsers = tweetDao.findTweetsFromFollowingUsers(
                user.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetsFromFollowingUsers, hasItems(tweetOne, tweetTwo, tweetThree));
    }

    @Test
    public void findTweetsFromFollowingUsers_orderTest() {
        User user = a(user());
        User followingOne = a(user().withFollowers(aListWith(user)));
        userDao.save(aListWith(user, followingOne));

        Tweet tweetOne = a(tweet()
                .withCreateDate(TestUtil.DATE_2000)
                .withOwner(followingOne)
        );
        Tweet tweetTwo = a(tweet()
                .withCreateDate(TestUtil.DATE_2001)
                .withOwner(followingOne)
        );
        Tweet tweetThree = a(tweet()
                .withCreateDate(TestUtil.DATE_2002)
                .withOwner(followingOne)
        );
        Tweet tweetFour = a(tweet()
                .withCreateDate(TestUtil.DATE_2003)
                .withOwner(followingOne)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree, tweetFour));

        List<Tweet> tweetsFromFollowingUsersPageOne = tweetDao.findTweetsFromFollowingUsers(
                user.getId(),
                new PageRequest(0, 2)
        );
        List<Tweet> tweetsFromFollowingUsersPageTwo = tweetDao.findTweetsFromFollowingUsers(
                user.getId(),
                new PageRequest(1, 2)
        );
        assertThat(tweetsFromFollowingUsersPageOne, contains(tweetFour, tweetThree));
        assertThat(tweetsFromFollowingUsersPageTwo, contains(tweetTwo, tweetOne));
    }

    @Test
    public void getFavouriteTweetsFromUser_oneUserSomeTweets() {
        User postOwner = a(user());

        Tweet tweetOne = a(tweet()
                .withOwner(postOwner)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(postOwner)
        );
        User user = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweetOne,
                                tweetTwo
                        )
                )
        );
        userDao.save(aListWith(postOwner, user));
        List<Tweet> userFavouriteTweets = tweetDao.findFavouriteTweetsFromUser(user.getId(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userFavouriteTweets, hasItems(tweetOne, tweetTwo));
    }


    @Test
    public void getFavouriteTweetsFromUser_someUserSomeTweets() {
        User postOwner = a(user());

        Tweet tweetOne = a(tweet()
                .withOwner(postOwner)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(postOwner)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(postOwner)
        );
        Tweet tweetFour = a(tweet()
                .withOwner(postOwner)
        );

        User userOne = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweetOne,
                                tweetTwo,
                                tweetThree,
                                tweetFour
                        )
                )
        );

        User userTwo = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweetOne,
                                tweetTwo
                        )
                )
        );
        userDao.save(aListWith(postOwner, userOne, userTwo));

        List<Tweet> userOneFavouriteTweets = tweetDao.findFavouriteTweetsFromUser(userOne.getId(), TestUtil.ALL_IN_ONE_PAGE);
        List<Tweet> userTwoFavouriteTweets = tweetDao.findFavouriteTweetsFromUser(userTwo.getId(), TestUtil.ALL_IN_ONE_PAGE);

        assertThat(userOneFavouriteTweets, hasItems(tweetOne, tweetTwo, tweetThree, tweetFour));
        assertThat(userTwoFavouriteTweets, hasItems(tweetOne, tweetTwo));
        assertThat(userOneFavouriteTweets, hasSize(4));
        assertThat(userTwoFavouriteTweets, hasSize(2));
    }

    @Test
    public void findUserFavouritePost_postIsNotInFavourites() {
        User user = a(user());

        User tweetOwner = a(user());
        userDao.save(aListWith(tweetOwner, user));

        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);

        boolean tweetInFavouritesTweets = tweetDao.doesTweetBelongToUserFavouritesTweets(user.getId(), tweet.getId());
        assertThat(tweetInFavouritesTweets, is(false));
    }

    @Test
    public void findUserFavouritePost_postIsInFavourites() {
        User tweetOwner = a(user());

        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );

        User user = a(user()
                .withFavouriteTweets(
                        aListWith(
                                tweet
                        )
                )
        );
        userDao.save(aListWith(tweetOwner, user));

        boolean tweetInFavouritesTweets = tweetDao.doesTweetBelongToUserFavouritesTweets(user.getId(), tweet.getId());
        assertThat(tweetInFavouritesTweets, is(true));
    }

    @Test
    public void findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc_findAllBySingleTag() {
        String TAG = "tag";
        User user = a(user());
        userDao.save(aListWith(user));
        Tag tagOne = a(tag().withText(TAG));
        Tweet tweetOne = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));
        List<Tweet> listWithTweetsWithTagOne = tweetDao.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(
                aListWith(
                        tagOne.getText()
                ),
                TestUtil.DATE_2001,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(listWithTweetsWithTagOne, hasItems(tweetOne, tweetTwo, tweetThree));
        assertThat(listWithTweetsWithTagOne, hasSize(3));
    }

    @Test
    public void findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc_findAllBySingleTag_orderByVoteSizeTest() {
        String TAG = "tag";
        User[] users = new User[]{
                a(user()), a(user()), a(user()), a(user()), a(user())
        };
        userDao.save(aListWith(users));
        Tag tagOne = a(tag().withText(TAG));
        Tweet tweetOne = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<UserVote> tweetOneVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetOne)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetOne))
        );

        List<UserVote> tweetTwoVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[4]).withAbstractPost(tweetTwo))
        );

        List<UserVote> tweetThreeVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetThree)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetThree)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetThree))
        );

        userVoteDao.save(Stream.of(tweetOneVotes, tweetTwoVotes, tweetThreeVotes).flatMap(Collection::stream).collect(Collectors.toList()));

        List<Tweet> listWithTweetsWithTagOne = tweetDao.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(
                aListWith(
                        tagOne.getText()
                ),
                TestUtil.DATE_2001,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(listWithTweetsWithTagOne, contains(tweetTwo, tweetThree, tweetOne));
        assertThat(listWithTweetsWithTagOne, hasSize(3));
    }

    @Test
    public void findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc_findAllBySingleTag_orderByCreateDateTest() {
        String TAG = "tag";
        User user = a(user());
        userDao.save(aListWith(user));
        Tag tagOne = a(tag().withText(TAG));
        Tweet tweetOne = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2003)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(user)
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2001)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));
        List<Tweet> listWithTweetsWithTagOne = tweetDao.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(
                aListWith(
                        tagOne.getText()
                ),
                TestUtil.DATE_2000,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(listWithTweetsWithTagOne, contains(tweetOne, tweetTwo, tweetThree));
        assertThat(listWithTweetsWithTagOne, hasSize(3));
    }

    @Test
    public void findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc_findAllBySingleTag_orderByVoteSizeAndCreateDateTest() {
        User[] users = new User[]{
                a(user()), a(user()), a(user()), a(user()), a(user())
        };
        userDao.save(aListWith(users));
        Tag tagOne = a(tag().withText("text"));
        Tweet tweetOne = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2003)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2002)
        );
        Tweet tweetThree = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2001)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<UserVote> tweetOneVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetOne)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetOne)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetOne))
        );

        List<UserVote> tweetTwoVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[4]).withAbstractPost(tweetTwo))
        );

        List<UserVote> tweetThreeVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetThree)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetThree)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetThree))
        );
        userVoteDao.save(Stream.of(tweetOneVotes, tweetTwoVotes, tweetThreeVotes).flatMap(Collection::stream).collect(Collectors.toList()));
        List<Tweet> listWithTweetsWithTagOne = tweetDao.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(
                aListWith(
                        tagOne.getText()
                ),
                TestUtil.DATE_2000,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(listWithTweetsWithTagOne, contains(tweetTwo, tweetOne, tweetThree));
        assertThat(listWithTweetsWithTagOne, hasSize(3));
    }

    @Test
    public void findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc_findAllByManyTags_allOrderTest() {
        User[] users = new User[]{
                a(user()), a(user()), a(user()), a(user()), a(user())
        };
        userDao.save(aListWith(users));
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        Tag tagThree = a(tag().withText("TAG_THREE"));
        Tweet tweetOne = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagOne))
                .withCreateDate(TestUtil.DATE_2003)
        );

        Tweet tweetTwo = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagTwo))
                .withCreateDate(TestUtil.DATE_2002)
        );

        Tweet tweetThree = a(tweet()
                .withOwner(users[0])
                .withTags(aListWith(tagThree))
                .withCreateDate(TestUtil.DATE_2001)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<UserVote> tweetOneVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetOne)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetOne)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetOne))
        );

        List<UserVote> tweetTwoVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetTwo)),
                a(userVote().withUser(users[4]).withAbstractPost(tweetTwo))
        );

        List<UserVote> tweetThreeVotes = aListWith(
                a(userVote().withUser(users[1]).withAbstractPost(tweetThree)),
                a(userVote().withUser(users[2]).withAbstractPost(tweetThree)),
                a(userVote().withUser(users[3]).withAbstractPost(tweetThree))
        );

        userVoteDao.save(Stream.of(tweetOneVotes, tweetTwoVotes, tweetThreeVotes).flatMap(Collection::stream).collect(Collectors.toList()));

        List<Tweet> listWithTweetsWithTagOne = tweetDao.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(
                aListWith(
                        tagOne.getText(),
                        tagTwo.getText(),
                        tagThree.getText()
                ),
                TestUtil.DATE_2000,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(listWithTweetsWithTagOne, contains(tweetTwo, tweetOne, tweetThree));
        assertThat(listWithTweetsWithTagOne, hasSize(3));
    }

    @Test
    public void findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc_tweetWithManyTagsShouldBeOnceInList() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        Tag tagThree = a(tag().withText("TAG_THREE"));
        Tweet tweetOne = a(tweet()
                .withOwner(user)
                .withTags(
                        aListWith(
                                tagOne,
                                tagTwo,
                                tagThree
                        ))
                .withCreateDate(TestUtil.DATE_2003)
        );

        tweetDao.save(tweetOne);

        List<Tweet> listWithTweetsWithTagOne = tweetDao.findByTagsTextInAndCreateDateAfterOrderByVotesVoteAscCreateDateDesc(
                aListWith(
                        tagOne.getText(),
                        tagTwo.getText(),
                        tagThree.getText()
                ),
                TestUtil.DATE_2000,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(listWithTweetsWithTagOne, contains(tweetOne));
        assertThat(listWithTweetsWithTagOne, hasSize(1));
    }
}
