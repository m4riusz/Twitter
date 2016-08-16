package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.dao.UserVoteDao;
import com.twitter.service.UserService;
import com.twitter.service.UserVoteService;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 16.08.16.
 */

@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class UserVoteServiceSecurityTest {

    @MockBean
    private UserService userService;
    @MockBean
    private UserVoteDao userVoteDao;
    @Autowired
    private UserVoteService userVoteService;

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void save_anonymousAccessDenied() {
        userVoteService.save(a(userVote()));
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void delete_anonymousAccessDenied() {
        userVoteService.delete(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getById_anonymousAccessDenied() {
        userVoteService.getById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void findUserVoteForPost_anonymousAccessDenied() {
        userVoteService.findUserVoteForPost(a(user()), a(tweet()));
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void exists_anonymousAccessDenied() {
        userVoteService.exists(TestUtil.ID_ONE);
    }
}
