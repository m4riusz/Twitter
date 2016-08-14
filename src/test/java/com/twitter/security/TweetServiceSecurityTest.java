package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.model.Tweet;
import com.twitter.model.Vote;
import com.twitter.service.TweetService;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.twitter.builders.PostVoteBuilder.postVote;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;
import static java.util.Collections.emptyList;

/**
 * Created by mariusz on 11.08.16.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class TweetServiceSecurityTest {

    @Autowired
    private TweetService tweetService;

    @WithCustomMockUser(id = TestUtil.ID_TWO, authorities = TestUtil.USER)
    @Test(expected = AccessDeniedException.class)
    public void create_wrongUserDenied() {
        Tweet tweet = a(tweet()
                .withOwner(
                        a(user()
                                .withId(TestUtil.ID_ONE)
                        )

                )
        );
        tweetService.create(tweet);
    }

    @WithCustomMockUser(authorities = TestUtil.ANONYMOUS)
    @Test(expected = AccessDeniedException.class)
    public void create_anonymousAccessDenied() {
        Tweet tweet = a(tweet()
                .withOwner(
                        a(user()
                                .withId(TestUtil.ID_ONE)
                        )

                )
        );
        tweetService.create(tweet);
    }


    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void delete_anonymousAccessDenied() {
        tweetService.delete(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void exists_anonymousAccessDenied() {
        tweetService.exists(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getById_anonymousAccessDenied() {
        tweetService.getById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void vote_anonymousAccessDenied() {
        tweetService.vote(a(postVote()
                        .withVote(Vote.UP)
                        .withPostId(TestUtil.ID_ONE)
                )
        );
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void deleteVote_anonymousAccessDenied() {
        tweetService.deleteVote(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllFromUserById_anonymousAccessDenied() {
        tweetService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllTweets_anonymousAccessDenied() {
        tweetService.getAllTweets(TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getTweetsFromFollowingUsers_anonymousAccessDenied() {
        tweetService.getTweetsFromFollowingUsers(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getMostVotedTweets_anonymousAccessDenied() {
        tweetService.getMostVotedTweets(10, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getTweetsByTagsOrderedByNewest_anonymousAccessDenied() {
        tweetService.getTweetsByTagsOrderedByNewest(emptyList(), TestUtil.ALL_IN_ONE_PAGE);
    }
}