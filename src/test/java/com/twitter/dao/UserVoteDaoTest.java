package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by mariusz on 17.08.16.
 */

@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserVoteDaoTest {

    @Autowired
    private UserVoteDao userVoteDao;

    @Autowired
    private TweetDao tweetDao;

    @Autowired
    private UserDao userDao;

    @Test
    public void findByUserAndAbstractPost_userDoesNotExist() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);
        UserVote userVote = userVoteDao.findByUserAndAbstractPost(null, tweet);
        assertThat(userVote, is(nullValue()));
    }

    @Test
    public void findByUserAndAbstractPost_postDoesNotExist() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        UserVote userVote = userVoteDao.findByUserAndAbstractPost(tweetOwner, null);
        assertThat(userVote, is(nullValue()));
    }

    @Test
    public void findByUserAndAbstractPost_userAndPostExistButVoteDoesNot() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);
        UserVote userVote = userVoteDao.findByUserAndAbstractPost(tweetOwner, tweet);
        assertThat(userVote, is(nullValue()));
    }

    @Test
    public void findByUserAndAbstractPost_voteExists() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);
        UserVote userVote = a(userVote()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
                .withVote(Vote.DOWN)
        );
        userVoteDao.save(userVote);
        UserVote userVoteFromDdb = userVoteDao.findByUserAndAbstractPost(tweetOwner, tweet);
        assertThat(userVoteFromDdb, is(userVote));
    }

    @Test
    public void countByAbstractPostIdAndVote_postDoesNotExist() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);
        UserVote userVote = a(userVote()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
                .withVote(Vote.DOWN)
        );
        userVoteDao.save(userVote);
        long userVoteFromDdb = userVoteDao.countByAbstractPostIdAndVote(TestUtil.ID_ONE, Vote.UP);
        assertThat(userVoteFromDdb, is(0L));
    }

    @Test
    public void countByAbstractPostIdAndVote_postExistsNoVotes() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);

        long userVoteFromDdb = userVoteDao.countByAbstractPostIdAndVote(tweet.getId(), Vote.UP);
        assertThat(userVoteFromDdb, is(0L));
    }

    @Test
    public void countByAbstractPostIdAndVote_postWithVoteUpWhenSearchingForVoteDownCount() {
        User tweetOwner = a(user());
        userDao.save(tweetOwner);
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);
        UserVote userVote = a(userVote()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
                .withVote(Vote.UP)
        );
        userVoteDao.save(userVote);
        long userVoteFromDdb = userVoteDao.countByAbstractPostIdAndVote(TestUtil.ID_ONE, Vote.DOWN);
        assertThat(userVoteFromDdb, is(0L));
    }

    @Test
    public void countByAbstractPostIdAndVote_postWithSomeVotesUpAndDown() {
        User tweetOwner = a(user());
        User tweetVoterOne = a(user());
        User tweetVoterTwo = a(user());
        User tweetVoterThree = a(user());

        userDao.save(aListWith(tweetOwner, tweetVoterOne, tweetVoterTwo, tweetVoterThree));
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        tweetDao.save(tweet);
        UserVote tweetOwnerVote = a(userVote()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
                .withVote(Vote.UP)
        );
        UserVote tweetVoterOneVote = a(userVote()
                .withUser(tweetVoterOne)
                .withAbstractPost(tweet)
                .withVote(Vote.UP)
        );
        UserVote tweetVoterTwoVote = a(userVote()
                .withUser(tweetVoterTwo)
                .withAbstractPost(tweet)
                .withVote(Vote.UP)
        );
        UserVote tweetVoterThreeVote = a(userVote()
                .withUser(tweetVoterThree)
                .withAbstractPost(tweet)
                .withVote(Vote.DOWN)
        );
        userVoteDao.save(aListWith(tweetOwnerVote, tweetVoterOneVote, tweetVoterTwoVote, tweetVoterThreeVote));
        long countOfVotesUp = userVoteDao.countByAbstractPostIdAndVote(tweet.getId(), Vote.UP);
        long countOfVotesDown = userVoteDao.countByAbstractPostIdAndVote(tweet.getId(), Vote.DOWN);
        assertThat(countOfVotesUp, is(3L));
        assertThat(countOfVotesDown, is(1L));
    }


}
