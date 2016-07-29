package com.twitter.dao;

import com.twitter.model.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 19.07.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TweetDaoTest {

    @Autowired
    private TweetDao tweetDao;

    @Autowired
    private UserDao userDao;

    @Test
    public void findAllTweetsByOwnerId_noTweets() {
        User user = a(user());
        userDao.save(user);
        List<Tweet> tweetsFromUser = tweetDao.findByOwnerId(user.getId(), new PageRequest(0, 10));
        assertThat(tweetsFromUser, is(emptyList()));
    }

    @Test
    public void findAllTweetsByOwnerId_someTweets() {
        User user = a(user());
        userDao.save(user);
        Tweet tweet1 = a(tweet().withOwner(user));
        Tweet tweet2 = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweet1, tweet2));
        List<Tweet> tweetsFromUser = tweetDao.findByOwnerId(user.getId(), new PageRequest(0, 10));
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

        List<Tweet> tweetsFromUserOne = tweetDao.findByOwnerId(userOne.getId(), new PageRequest(0, 10));
        List<Tweet> tweetsFromUserTwo = tweetDao.findByOwnerId(userTwo.getId(), new PageRequest(0, 10));
        List<Tweet> tweetsFromUserThree = tweetDao.findByOwnerId(userThree.getId(), new PageRequest(0, 10));
        assertThat(tweetsFromUserOne, hasItems(tweet1, tweet2, tweet3));
        assertThat(tweetsFromUserTwo, hasItems(tweet4, tweet5));
        assertThat(tweetsFromUserThree, is(emptyList()));
    }

    @Test
    public void findMostPopularByVotes_noTweets() {
        List<Tweet> mostPopular = tweetDao.findMostPopularByVotes(24, new PageRequest(0, 10));
        assertThat(mostPopular, is(emptyList()));
    }

    @Test
    public void findMostPopularByVotes_tooOldTweet() {
        User user = a(user());
        userDao.save(aListWith(user));
        Date tooOldDate = DateTime.now().minusHours(8).minusMinutes(1).toDate();
        Tweet tweet = a(tweet().withOwner(user).withCreateDate(tooOldDate));
        tweetDao.save(aListWith(tweet));
        List<Tweet> mostPopularTweetList = tweetDao.findMostPopularByVotes(8, new PageRequest(0, 10));
        assertThat(mostPopularTweetList, is(emptyList()));
    }

    @Test
    public void findMostPopularByVotes_newTweet() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweet));
        List<Tweet> mostPopularTweetList = tweetDao.findMostPopularByVotes(8, new PageRequest(0, 10));
        assertThat(mostPopularTweetList, hasItem(tweet));
    }

    @Test
    public void findMostPopularByVotes_someOldAndNewTweets(){
        User user = a(user());

        userDao.save(aListWith(user));
        Date tooOldDate = DateTime.now().minusDays(10).toDate();
        Date currentDate = DateTime.now().toDate();
        Tweet tweetOne = a(tweet().withOwner(user).withCreateDate(tooOldDate));
        Tweet tweetTwo = a(tweet().withOwner(user).withCreateDate(currentDate));
        Tweet tweetThree = a(tweet().withOwner(user).withCreateDate(currentDate));

        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<Tweet> mostPopularTweets = tweetDao.findMostPopularByVotes(10, new PageRequest(0, 10));

        assertThat(mostPopularTweets, contains(tweetTwo, tweetThree));
        assertThat(mostPopularTweets, not(hasItem(tweetOne)));
    }

    @Test
    public void findMostPopularByVotes_unpopularAndPopularTweets() {
        User user = a(user());
        User user1 = a(user());
        User user2 = a(user());
        User user3 = a(user());

        userDao.save(aListWith(user, user1, user2, user3));
        Tweet tweetOne = a(tweet().withOwner(user));
        Tweet tweetTwo = a(tweet().withOwner(user));
        Tweet tweetThree = a(tweet().withOwner(user));

        UserVote voteTweetOneUserOne = a(userVote().withUser(user1).withVote(Vote.UP).withAbstractPost(tweetOne));
        UserVote voteTweetOneUserTwo = a(userVote().withUser(user2).withVote(Vote.UP).withAbstractPost(tweetOne));
        UserVote voteTweetTwoUserOne = a(userVote().withUser(user1).withVote(Vote.UP).withAbstractPost(tweetTwo));
        UserVote voteTweetTwoUserTwo = a(userVote().withUser(user2).withVote(Vote.UP).withAbstractPost(tweetTwo));
        UserVote voteTweetTwoUserThree = a(userVote().withUser(user3).withVote(Vote.UP).withAbstractPost(tweetTwo));
        UserVote voteTweetThreeUserOne = a(userVote().withUser(user1).withVote(Vote.UP).withAbstractPost(tweetThree));

        tweetOne.setVotes(aListWith(voteTweetOneUserOne, voteTweetOneUserTwo));
        tweetTwo.setVotes(aListWith(voteTweetTwoUserOne, voteTweetTwoUserTwo, voteTweetTwoUserThree));
        tweetThree.setVotes(aListWith(voteTweetThreeUserOne));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<Tweet> mostPopularOnFirstPage = tweetDao.findMostPopularByVotes(24, new PageRequest(0, 2));
        List<Tweet> mostPopularSecondPage = tweetDao.findMostPopularByVotes(24, new PageRequest(1, 2));

        assertThat(mostPopularOnFirstPage, contains(tweetTwo, tweetOne));
        assertThat(mostPopularSecondPage, contains(tweetThree));
    }

    @Test
    public void findByTagsInOrderByCreateDateDesc_noTags() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet().withOwner(user).withTags(emptyList()));
        tweetDao.save(aListWith(tweet));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(emptyList(), new PageRequest(0, 10));
        assertThat(tweetsWithTag, is(emptyList()));
    }

    @Test
    public void findByTagsInOrderByCreateDateDesc_oneTag() {
        Tag tag = a(tag().withText("tag"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet().withOwner(user).withTags(aListWith(tag)));
        tweetDao.save(aListWith(tweet));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tag), new PageRequest(0, 10));
        assertThat(tweetsWithTag, hasItem(tweet));
    }


    @Test
    public void findByTagsInOrderByCreateDateDesc_oneTweetWithSomeTagsFindUsingAllTags() {
        Tag tagOne = a(tag().withText("tag1"));
        Tag tagTwo = a(tag().withText("tag2"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweet = a(tweet().withOwner(user).withTags(aListWith(tagOne, tagTwo)));
        tweetDao.save(aListWith(tweet));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tagOne, tagTwo), new PageRequest(0, 10));
        assertThat(tweetsWithTag, hasItem(tweet));
        assertThat(tweetsWithTag, hasSize(1));
    }

    @Test
    public void findByTagsInOrderByCreateDateDesc_oneTagAndManyTweets() {
        Tag tag = a(tag().withText("tag1"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweetOne = a(tweet().withOwner(user).withTags(aListWith(tag)));
        Tweet tweetTwo = a(tweet().withOwner(user).withTags(aListWith(tag)));
        tweetDao.save(aListWith(tweetOne, tweetTwo));
        List<Tweet> tweetsWithTag = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tag), new PageRequest(0, 10));
        assertThat(tweetsWithTag, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void findByTagsInOrderByCreateDateDesc_tweetOrderedByNewest() {
        Tag tag = a(tag().withText("tag"));
        User user = a(user());
        userDao.save(aListWith(user));
        Tweet tweetOne = a(tweet().withCreateDate(DateTime.now().minusDays(1).toDate()).withOwner(user).withTags(aListWith(tag)));
        Tweet tweetTwo = a(tweet().withCreateDate(DateTime.now().minusDays(2).toDate()).withOwner(user).withTags(aListWith(tag)));
        Tweet tweetThree = a(tweet().withCreateDate(DateTime.now().minusDays(3).toDate()).withOwner(user).withTags(aListWith(tag)));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));
        List<Tweet> firstPageTweets = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tag), new PageRequest(0, 2));
        List<Tweet> secondPageTweets = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tag), new PageRequest(1, 2));
        assertThat(firstPageTweets, contains(tweetOne, tweetTwo));
        assertThat(secondPageTweets, contains(tweetThree));
    }

    @Test
    public void findByTagsInOrderByCreateDateDesc_manyTweetsWithDifferentTagsFindAllByTags() {
        User user = a(user());
        userDao.save(aListWith(user));
        Tag tagOne = a(tag().withText("tag1"));
        Tag tagTwo = a(tag().withText("tag2"));
        Tag tagThree = a(tag().withText("tag3"));
        Tweet tweetOne = a(tweet().withOwner(user).withTags(aListWith(tagOne)));
        Tweet tweetTwo = a(tweet().withOwner(user).withTags(aListWith(tagTwo)));
        Tweet tweetThree = a(tweet().withOwner(user).withTags(aListWith(tagThree)));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));
        List<Tweet> tweetsWithTags = tweetDao.findDistinctByTagsInOrderByCreateDateDesc(aListWith(tagOne, tagTwo, tagThree), new PageRequest(0, 10));
        assertThat(tweetsWithTags, hasItems(tweetOne, tweetTwo, tweetThree));
        assertThat(tweetsWithTags, hasSize(3));
    }

    @Test
    public void findTweetsFromFollowingUsers_noFollowingUsers() {
        User user = a(user());
        userDao.save(user);
        List<Tweet> tweetsFromFollowingUsers = tweetDao.findTweetsFromFollowingUsers(user.getId(), new PageRequest(0, 10));
        assertThat(tweetsFromFollowingUsers, is(emptyList()));
    }

    @Test
    public void findTweetsFromFollowingUsers_oneFollowingAndSomeTweets() {
        User user = a(user());
        User followingOne = a(user().withFollowers(aListWith(user)));
        userDao.save(aListWith(user, followingOne));

        Tweet tweetOne = a(tweet().withOwner(followingOne));
        Tweet tweetTwo = a(tweet().withOwner(followingOne));
        tweetDao.save(aListWith(tweetOne, tweetTwo));

        List<Tweet> tweetsFromFollowingUsers = tweetDao.findTweetsFromFollowingUsers(user.getId(), new PageRequest(0, 10));
        assertThat(tweetsFromFollowingUsers, hasItems(tweetOne, tweetTwo));
    }

    @Test
    public void findTweetsFromFollowingUsers_someFollowingAndSomeTweets() {
        User user = a(user());
        User followingOne = a(user().withFollowers(aListWith(user)));
        User followingTwo = a(user().withFollowers(aListWith(user)));
        userDao.save(aListWith(user, followingOne, followingTwo));

        Tweet tweetOne = a(tweet().withOwner(followingOne));
        Tweet tweetTwo = a(tweet().withOwner(followingOne));
        Tweet tweetThree = a(tweet().withOwner(followingTwo));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        List<Tweet> tweetsFromFollowingUsers = tweetDao.findTweetsFromFollowingUsers(user.getId(), new PageRequest(0, 10));
        assertThat(tweetsFromFollowingUsers, hasItems(tweetOne, tweetTwo, tweetThree));
    }

    @Test
    public void findTweetsFromFollowingUsers_orderTest() {
        User user = a(user());
        User followingOne = a(user().withFollowers(aListWith(user)));
        userDao.save(aListWith(user, followingOne));
        Date oldestDate = DateTime.now().minusDays(4).toDate();
        Date oldDate = DateTime.now().minusDays(3).toDate();
        Date date = DateTime.now().minusDays(2).toDate();
        Date youngestDate = DateTime.now().toDate();
        Tweet tweetOne = a(tweet().withCreateDate(oldestDate).withOwner(followingOne));
        Tweet tweetTwo = a(tweet().withCreateDate(oldDate).withOwner(followingOne));
        Tweet tweetThree = a(tweet().withCreateDate(date).withOwner(followingOne));
        Tweet tweetFour = a(tweet().withCreateDate(youngestDate).withOwner(followingOne));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree, tweetFour));

        List<Tweet> tweetsFromFollowingUsersPageOne = tweetDao.findTweetsFromFollowingUsers(user.getId(), new PageRequest(0, 2));
        List<Tweet> tweetsFromFollowingUsersPageTwo = tweetDao.findTweetsFromFollowingUsers(user.getId(), new PageRequest(1, 2));
        assertThat(tweetsFromFollowingUsersPageOne, contains(tweetFour, tweetThree));
        assertThat(tweetsFromFollowingUsersPageTwo, contains(tweetTwo, tweetOne));
    }


}
