package com.twitter.dao;

import com.twitter.model.Tweet;
import com.twitter.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
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
        User userOne = a(user().withUsername("User1"));
        User userTwo = a(user().withUsername("User2"));
        User userThree = a(user().withUsername("User3"));

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
    public void findCommentsByTweetId_noComments() {
        User user = a(user());
        userDao.save(user);

        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(tweet);
        List<Tweet> commentsFromTweet = tweetDao.findCommentsById(tweet.getId(), new PageRequest(0, 10));
        assertThat(commentsFromTweet, is(emptyList()));
    }

    @Test
    public void findCommentsByTweetId_someComments() {
        User user = a(user().withUsername("Owner"));
        User commentator = a(user().withUsername("Commentator"));
        userDao.save(aListWith(user, commentator));

        Tweet comment = a(tweet().withOwner(commentator));
        Tweet tweet = a(tweet().withOwner(user).withComments(aListWith(comment)));
        tweetDao.save(aListWith(comment, tweet));
        List<Tweet> commentsFromTweet = tweetDao.findCommentsById(tweet.getId(), new PageRequest(0, 10));
        assertThat(commentsFromTweet, hasItem(comment));
        assertThat(commentsFromTweet.get(0).getOwner(), is(commentator));
    }

    @Test
    public void findCommentsByTweetId_someCommentsAndCommentators() {
        User userOne = a(user().withUsername("Owner1"));
        User userTwo = a(user().withUsername("Owner2"));
        User commentator1 = a(user().withUsername("Commentator1"));
        User commentator2 = a(user().withUsername("Commentator2"));
        User commentator3 = a(user().withUsername("Commentator3"));
        User commentator4 = a(user().withUsername("Commentator4"));
        userDao.save(aListWith(userOne, userTwo, commentator1, commentator2, commentator3, commentator4));

        Tweet comment1 = a(tweet().withOwner(commentator1));
        Tweet comment2 = a(tweet().withOwner(commentator2));
        Tweet comment3 = a(tweet().withOwner(commentator3));
        Tweet comment4 = a(tweet().withOwner(commentator4));
        Tweet ownerComment = a(tweet().withOwner(userOne));
        Tweet tweetFromUserOne = a(tweet().withOwner(userOne).withComments(aListWith(comment1, comment2, comment3, ownerComment)));
        Tweet tweetFromUserTwo = a(tweet().withOwner(userTwo).withComments(aListWith(comment4)));
        tweetDao.save(aListWith(comment1, comment2, comment3, comment4, ownerComment, tweetFromUserOne, tweetFromUserTwo));

        List<Tweet> commentsFromTweetOne = tweetDao.findCommentsById(tweetFromUserOne.getId(), new PageRequest(0, 10));
        List<Tweet> commentsFromTweetTwo = tweetDao.findCommentsById(tweetFromUserTwo.getId(), new PageRequest(0, 10));
        assertThat(commentsFromTweetOne, hasItems(comment1, comment2, comment3, ownerComment));
        assertThat(commentsFromTweetTwo, hasItem(comment4));
    }


}
