package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.UserVoteDao;
import com.twitter.exception.UserVoteException;
import com.twitter.exception.UserVoteNotFoundException;
import com.twitter.model.AbstractPost;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.model.UserVote;
import com.twitter.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;


/**
 * Created by mariusz on 16.08.16.
 */
@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@RunWith(MockitoJUnitRunner.class)
public class UserVoteServiceTest {

    @Mock
    private UserVoteDao userVoteDao;
    @Mock
    private UserService userService;

    private UserVoteService userVoteService;

    @Before
    public void setUp() {
        userVoteService = new UserVoteServiceImpl(userVoteDao, userService);
    }

    @Test
    public void save_createSuccess() {
        UserVote userVote = a(userVote()
                .withUser(a(user()))
                .withAbstractPost(a(tweet()))
        );
        when(userVoteDao.save(any(UserVote.class))).thenReturn(userVote);
        UserVote savedUserVote = userVoteService.save(userVote);
        assertThat(savedUserVote, is(userVote));
    }

    @Test
    public void delete_userVoteDeletedByVoteOwner() {
        User voteOwner = a(user());
        UserVote userVote = a(userVote()
                .withUser(voteOwner)
                .withAbstractPost(a(tweet()))
        );
        when(userVoteDao.exists(anyLong())).thenReturn(true);
        when(userVoteDao.findOne(anyLong())).thenReturn(userVote);
        when(userService.getCurrentLoggedUser()).thenReturn(voteOwner);
        userVoteService.delete(userVote.getId());
        verify(userVoteDao, times(1)).delete(any(UserVote.class));
    }

    @Test(expected = UserVoteException.class)
    public void delete_userVoteExistsWrongUser() {
        User user = a(user());
        User differentUser = a(user());

        UserVote userVote = a(userVote()
                .withUser(user)
                .withAbstractPost(a(tweet()))
        );
        when(userVoteDao.exists(anyLong())).thenReturn(true);
        when(userVoteDao.findOne(anyLong())).thenReturn(userVote);
        when(userService.getCurrentLoggedUser()).thenReturn(differentUser);
        userVoteService.delete(userVote.getId());
    }

    @Test(expected = UserVoteException.class)
    public void delete_userVoteDoesNotExist() {
        UserVote userVote = a(userVote());
        when(userVoteDao.exists(anyLong())).thenReturn(false);
        userVoteService.delete(userVote.getId());
    }

    @Test
    public void getById_userVoteExists() {
        UserVote userVote = a(userVote()
                .withUser(a(user()))
                .withAbstractPost(a(tweet()))
        );
        when(userVoteDao.exists(anyLong())).thenReturn(true);
        when(userVoteDao.findOne(anyLong())).thenReturn(userVote);
        UserVote userVoteFromDb = userVoteService.getById(userVote.getId());
        assertThat(userVoteFromDb, is(userVote));
    }

    @Test(expected = UserVoteNotFoundException.class)
    public void getById_userVoteDoesNotExist() {
        UserVote userVote = a(userVote());
        when(userVoteDao.exists(anyLong())).thenReturn(false);
        userVoteService.getById(userVote.getId());
    }

    @Test
    public void findUserVoteForPost_userVoteDoesNotExist() {
        User user = a(user());
        Tweet tweet = a(tweet());
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(null);
        UserVote userVote = userVoteService.findUserVoteForPost(user, tweet);
        assertThat(userVote, is(nullValue()));
    }

    @Test
    public void findUserVoteForPost_userVoteExists() {
        User user = a(user());
        Tweet tweet = a(tweet());
        UserVote userVote = a(userVote()
                .withUser(user)
                .withAbstractPost(tweet)
        );
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(userVote);
        UserVote userVoteForPost = userVoteService.findUserVoteForPost(user, tweet);
        assertThat(userVoteForPost, is(userVote));
    }

    @Test
    public void exists_userVoteDoesNotExist() {
        when(userVoteDao.exists(anyLong())).thenReturn(false);
        boolean exists = userVoteService.exists(TestUtil.ID_ONE);
        assertThat(exists, is(false));
    }

    @Test
    public void exists_userVoteExists() {
        when(userVoteDao.exists(anyLong())).thenReturn(true);
        boolean exists = userVoteService.exists(TestUtil.ID_ONE);
        assertThat(exists, is(true));
    }
}
