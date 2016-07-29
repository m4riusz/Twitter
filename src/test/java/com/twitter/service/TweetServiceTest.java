package com.twitter.service;

import com.twitter.dao.TweetDao;
import com.twitter.dao.UserDao;
import com.twitter.exception.TweetNotFoundException;
import com.twitter.model.Result;
import com.twitter.model.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static com.twitter.Util.a;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 29.07.16.
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TweetServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private TweetDao tweetDao;

    private TweetService tweetService;

    @Before
    public void setUp() {
        tweetService = new TweetServiceImpl(tweetDao, userDao);
    }

    @Test(expected = TweetNotFoundException.class)
    public void getTweetById_tweetDoesNotExist() {
        when(tweetDao.exists(anyLong())).thenReturn(false);
        tweetService.getTweetById(1L);
    }

    @Test
    public void getTweetById_tweetExists() {
        when(tweetDao.exists(anyLong())).thenReturn(true);
        Tweet tweet = a(tweet().withOwner(a(user())));
        when(tweetDao.findOne(anyLong())).thenReturn(tweet);
        Result<Tweet> tweetById = tweetService.getTweetById(1L);
        assertThat(tweetById, hasFinishedSuccessfully());
        assertThat(tweetById, hasValueOf(tweet));

    }


}
