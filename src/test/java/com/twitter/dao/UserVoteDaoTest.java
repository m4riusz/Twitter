package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.model.Vote;
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

}
