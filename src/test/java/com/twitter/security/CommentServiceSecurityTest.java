package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.model.Comment;
import com.twitter.model.Vote;
import com.twitter.service.CommentService;
import com.twitter.util.TestUtil;
import com.twitter.util.WithCustomMockUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.twitter.builders.CommentBuilder.comment;
import static com.twitter.builders.PostVoteBuilder.postVote;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 12.08.16.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class CommentServiceSecurityTest {

    @Autowired
    private CommentService commentService;

    @WithCustomMockUser(id = TestUtil.ID_TWO, authorities = TestUtil.USER)
    @Test(expected = AccessDeniedException.class)
    public void create_wrongUserDenied() {
        Comment comment = a(comment()
                .withOwner(
                        a(user()
                                .withId(TestUtil.ID_ONE)
                        )
                )
                .withTweet(a(tweet()))
        );
        commentService.create(comment);
    }

    @WithCustomMockUser(authorities = TestUtil.ANONYMOUS)
    @Test(expected = AccessDeniedException.class)
    public void create_anonymousAccessDenied() {
        Comment comment = a(comment()
                .withOwner(
                        a(user()
                                .withId(TestUtil.ID_ONE)
                        )

                )
        );
        commentService.create(comment);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void delete_anonymousAccessDenied() {
        commentService.delete(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void exists_anonymousAccessDenied() {
        commentService.exists(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getById_anonymousAccessDenied() {
        commentService.getById(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void vote_anonymousAccessDenied() {
        commentService.vote(a(postVote()
                        .withVote(Vote.UP)
                        .withPostId(TestUtil.ID_ONE)
                )
        );
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void deleteVote_anonymousAccessDenied() {
        commentService.deleteVote(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getAllFromUserById_anonymousAccessDenied() {
        commentService.getAllFromUserById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getTweetCommentsById_anonymousAccessDenied() {
        commentService.getTweetCommentsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getLatestCommentsById_anonymousAccessDenied() {
        commentService.getLatestCommentsById(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getOldestCommentsById_anonymousAccessDenied() {
        commentService.getOldestCommentsById(10, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getMostVotedComments_anonymousAccessDenied() {
        commentService.getMostVotedComments(TestUtil.ID_ONE, TestUtil.ALL_IN_ONE_PAGE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getPostVote_anonymousAccessDenied() {
        commentService.getPostVote(TestUtil.ID_ONE);
    }

    @WithAnonymousUser
    @Test(expected = AccessDeniedException.class)
    public void getPostVoteCount_anonymousAccessDenied() {
        commentService.getPostVoteCount(TestUtil.ID_ONE, Vote.UP);
    }

}
