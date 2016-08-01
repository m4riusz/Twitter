package com.twitter.dao;

import com.twitter.model.Comment;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.service.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.CommentBuilder.comment;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
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
    public void findByTweetId_TweetWithnoComments() {
        User user = a(user());
        userDao.save(user);
        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(tweet);
        List<Comment> commentsFromTweet = commentDao.findByTweetId(
                tweet.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentsFromTweet, is(emptyList()));
    }

    @Test
    public void findByTweetId_tweetIdNotFound() {
        List<Comment> commentsFromNotExistingTweet = commentDao.findByTweetId(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentsFromNotExistingTweet, is(emptyList()));
    }

    @Test
    public void findByTweetId_oneComment() {
        User user = a(user());
        User commentator = a(user());
        userDao.save(aListWith(user, commentator));
        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweet));
        Comment comment = a(comment()
                .withTweet(tweet)
                .withOwner(commentator)
        );
        commentDao.save(aListWith(comment));
        List<Comment> commentsFromTweet = commentDao.findByTweetId(
                tweet.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentsFromTweet, hasItem(comment));
    }

    @Test
    public void findByTweetId_someTweetsAndSomeComments() {
        User userOne = a(user());
        User userTwo = a(user());
        User commentator1 = a(user());
        User commentator2 = a(user());
        User commentator3 = a(user());
        User commentator4 = a(user());
        userDao.save(aListWith(userOne, userTwo, commentator1, commentator2, commentator3, commentator4));
        Tweet tweetFromUserOne = a(tweet().withOwner(userOne));
        Tweet tweetFromUserTwo = a(tweet().withOwner(userTwo));
        tweetDao.save(aListWith(tweetFromUserOne, tweetFromUserTwo));
        Comment comment1 = a(comment()
                .withOwner(commentator1)
                .withTweet(tweetFromUserOne)
        );
        Comment comment2 = a(comment()
                .withOwner(commentator2)
                .withTweet(tweetFromUserOne)
        );
        Comment comment3 = a(comment()
                .withOwner(commentator3)
                .withTweet(tweetFromUserOne)
        );
        Comment comment4 = a(comment()
                .withOwner(commentator4)
                .withTweet(tweetFromUserTwo)
        );
        Comment ownerComment = a(comment()
                .withOwner(userOne)
                .withTweet(tweetFromUserOne)
        );
        commentDao.save(aListWith(comment1, comment2, comment3, comment4, ownerComment));

        List<Comment> commentsFromTweetOne = commentDao.findByTweetId(
                tweetFromUserOne.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        List<Comment> commentsFromTweetTwo = commentDao.findByTweetId(
                tweetFromUserTwo.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentsFromTweetOne, hasItems(comment1, comment2, comment3, ownerComment));
        assertThat(commentsFromTweetTwo, hasItem(comment4));
    }


    @Test
    public void findByTweetIdOrderByVotesSize_someCommentsWithDifferentAmountOfVotes() {
        User userOne = a(user());
        User commentator = a(user());
        userDao.save(aListWith(userOne, commentator));
        Tweet tweetFromUserOne = a(tweet().withOwner(userOne));
        tweetDao.save(aListWith(tweetFromUserOne));

        Comment comment1 = a(comment()
                .withOwner(commentator)
                .withTweet(tweetFromUserOne)
                .withVotes(aListWith(
                        a(userVote().withAbstractPost(tweetFromUserOne).withUser(commentator)),
                        a(userVote().withAbstractPost(tweetFromUserOne).withUser(commentator)),
                        a(userVote().withAbstractPost(tweetFromUserOne).withUser(commentator))
                )));
        Comment comment2 = a(comment()
                .withOwner(commentator)
                .withTweet(tweetFromUserOne)
                .withVotes(aListWith(
                        a(userVote().withAbstractPost(tweetFromUserOne).withUser(commentator))
                )));
        Comment comment3 = a(comment()
                .withOwner(commentator)
                .withTweet(tweetFromUserOne)
                .withVotes(emptyList()));
        commentDao.save(aListWith(comment1, comment2, comment3));

        List<Comment> commentsFromTweetOne = commentDao.findByTweetIdOrderByVotes(
                tweetFromUserOne.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentsFromTweetOne, contains(comment1, comment2, comment3));
    }

    @Test
    public void findByTweetIdOrderByCreateDateAsc_orderByOldestCommentsTest() {
        User user = a(user());
        User commentator = a(user());
        userDao.save(aListWith(user, commentator));
        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweet));

        Comment youngestComment = a(comment()
                .withOwner(commentator)
                .withTweet(tweet)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Comment olderComment = a(comment()
                .withOwner(commentator)
                .withTweet(tweet)
                .withCreateDate(TestUtil.DATE_2002)
        );
        Comment oldestComment = a(comment()
                .withOwner(commentator)
                .withTweet(tweet)
                .withCreateDate(TestUtil.DATE_2001)
        );
        commentDao.save(aListWith(youngestComment, olderComment, oldestComment));

        List<Comment> commentList = commentDao.findByTweetIdOrderByCreateDateAsc(
                tweet.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentList, contains(oldestComment, olderComment, youngestComment));
    }

    @Test
    public void findByTweetIdOrderByCreateDateDesc_orderByNewestCommentsTest() {
        User user = a(user());
        User commentator = a(user());
        userDao.save(aListWith(user, commentator));
        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(aListWith(tweet));

        Comment youngestComment = a(comment()
                .withOwner(commentator)
                .withTweet(tweet)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Comment olderComment = a(comment()
                .withOwner(commentator)
                .withTweet(tweet)
                .withCreateDate(TestUtil.DATE_2002)
        );
        Comment oldestComment = a(comment()
                .withOwner(commentator)
                .withTweet(tweet)
                .withCreateDate(TestUtil.DATE_2001)
        );
        commentDao.save(aListWith(youngestComment, olderComment, oldestComment));

        List<Comment> commentList = commentDao.findByTweetIdOrderByCreateDateDesc(
                tweet.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentList, contains(youngestComment, olderComment, oldestComment));
    }

}
