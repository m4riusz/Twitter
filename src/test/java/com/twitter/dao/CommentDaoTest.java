package com.twitter.dao;

import com.twitter.model.Comment;
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
import static com.twitter.builders.CommentBuilder.comment;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 21.07.16.
 */

@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentDaoTest {

    @Autowired
    private TweetDao tweetDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;

    @Test
    public void findCommentsByTweetId_noComments() {
        User user = a(user());
        userDao.save(user);

        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(tweet);
        List<Comment> commentsFromTweet = commentDao.findCommentsById(tweet.getId(), new PageRequest(0, 10));
        assertThat(commentsFromTweet, is(emptyList()));
    }

    @Test
    public void findCommentsByTweetId_someComments() {
        User user = a(user().withUsername("Owner"));
        User commentator = a(user().withUsername("Commentator"));
        userDao.save(aListWith(user, commentator));

        Tweet tweet = a(tweet().withOwner(user));
        Comment comment = a(comment().withTweet(tweet).withOwner(commentator));
        commentDao.save(aListWith(comment));
        tweetDao.save(aListWith(tweet));
        List<Comment> commentsFromTweet = commentDao.findCommentsById(tweet.getId(), new PageRequest(0, 10));
        assertThat(commentsFromTweet, hasItem(comment));
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

        Tweet tweetFromUserOne = a(tweet().withOwner(userOne));
        Tweet tweetFromUserTwo = a(tweet().withOwner(userTwo));
        Comment comment1 = a(comment().withOwner(commentator1).withTweet(tweetFromUserOne));
        Comment comment2 = a(comment().withOwner(commentator2).withTweet(tweetFromUserOne));
        Comment comment3 = a(comment().withOwner(commentator3).withTweet(tweetFromUserOne));
        Comment comment4 = a(comment().withOwner(commentator4).withTweet(tweetFromUserTwo));
        Comment ownerComment = a(comment().withOwner(userOne).withTweet(tweetFromUserOne));
        commentDao.save(aListWith(comment1, comment2, comment3, comment4, ownerComment));
        tweetDao.save(aListWith(tweetFromUserOne, tweetFromUserTwo));

        List<Comment> commentsFromTweetOne = commentDao.findCommentsById(tweetFromUserOne.getId(), new PageRequest(0, 10));
        List<Comment> commentsFromTweetTwo = commentDao.findCommentsById(tweetFromUserTwo.getId(), new PageRequest(0, 10));
        assertThat(commentsFromTweetOne, hasItems(comment1, comment2, comment3, ownerComment));
        assertThat(commentsFromTweetTwo, hasItem(comment4));
    }

}
