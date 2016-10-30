package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.Comment;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.builders.CommentBuilder.comment;
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
 * Created by mariusz on 21.07.16.
 */

@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentDaoTest {

    @Autowired
    private TweetDao tweetDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserVoteDao userVoteDao;

    @Autowired
    private CommentDao commentDao;

    @Test
    public void findByTweetId_TweetWithNoComments() {
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
        User commentatorOne = a(user());
        User commentatorTwo = a(user());
        User commentatorThree = a(user());
        userDao.save(aListWith(userOne, commentatorOne, commentatorTwo, commentatorThree));
        Tweet tweetFromUserOne = a(tweet().withOwner(userOne));
        tweetDao.save(aListWith(tweetFromUserOne));

        Comment commentOne = a(comment()
                .withOwner(commentatorOne)
                .withTweet(tweetFromUserOne)
        );
        Comment commentTwo = a(comment()
                .withOwner(commentatorTwo)
                .withTweet(tweetFromUserOne)
        );
        Comment commentThree = a(comment()
                .withOwner(commentatorThree)
                .withTweet(tweetFromUserOne)
        );
        commentDao.save(aListWith(commentOne, commentTwo, commentThree));


        List<UserVote> commentTwoVotes = aListWith(
                a(userVote().withAbstractPost(commentTwo).withUser(commentatorOne)),
                a(userVote().withAbstractPost(commentTwo).withUser(commentatorTwo)),
                a(userVote().withAbstractPost(commentTwo).withUser(commentatorThree))
        );
        List<UserVote> commentOneVotes = aListWith(
                a(userVote().withAbstractPost(commentOne).withUser(commentatorOne)),
                a(userVote().withAbstractPost(commentOne).withUser(commentatorTwo))
        );

        List<UserVote> commentThreeVotes = aListWith(
                a(userVote().withAbstractPost(commentThree).withUser(commentatorThree))
        );

        userVoteDao.save(commentOneVotes);
        userVoteDao.save(commentTwoVotes);
        userVoteDao.save(commentThreeVotes);

        List<Comment> commentsFromTweetOne = commentDao.findByTweetIdOrderByVotesAscCreateDateDesc(
                tweetFromUserOne.getId(),
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(commentsFromTweetOne, contains(commentTwo, commentOne, commentThree));
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

    @Test
    public void findByOwnerId_userDoesNotExist() {
        List<Comment> commentList = commentDao.findByOwnerId(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(commentList, is(emptyList()));
    }

    @Test
    public void findByOwnerId_userExistSomeComments() {
        User user = a(user());
        User commentatorOne = a(user());
        User commentatorTwo = a(user());
        userDao.save(aListWith(user, commentatorOne, commentatorTwo));

        Tweet tweet = a(tweet().withOwner(user));
        tweetDao.save(tweet);

        Comment commentOne = a(comment()
                .withOwner(commentatorOne)
                .withTweet(tweet)
        );

        Comment commentTwo = a(comment()
                .withOwner(commentatorOne)
                .withTweet(tweet)
        );

        Comment commentThree = a(comment()
                .withOwner(commentatorTwo)
                .withTweet(tweet)
        );
        commentDao.save(aListWith(commentOne, commentTwo, commentThree));
        List<Comment> commentsFromCommentatorOne = commentDao.findByOwnerId(commentatorOne.getId(), TestUtil.ALL_IN_ONE_PAGE);
        List<Comment> commentsFromCommentatorTwo = commentDao.findByOwnerId(commentatorTwo.getId(), TestUtil.ALL_IN_ONE_PAGE);
        assertThat(commentsFromCommentatorOne, hasItems(commentOne, commentTwo));
        assertThat(commentsFromCommentatorTwo, hasItem(commentThree));
    }
}
