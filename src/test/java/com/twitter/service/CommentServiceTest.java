package com.twitter.service;

import com.twitter.dao.CommentDao;
import com.twitter.dao.UserDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.CommentBuilder.comment;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.matchers.ResultIsFailureMatcher.hasFailed;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultMessageMatcher.hasMessageOf;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 03.08.16.
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {
    @Mock
    private TweetService tweetService;
    @Mock
    private CommentDao commentDao;
    @Mock
    private UserDao userDao;
    @Mock
    private UserVoteDao userVoteDao;

    private CommentService commentService;

    @Before
    public void setUp() {
        commentService = new CommentServiceImpl(commentDao, tweetService, userVoteDao, userDao);
    }

    @Test
    public void createComment_test() {
        Comment comment = a(comment());
        when(commentDao.save(any(Comment.class))).thenReturn(comment);
        Result<Boolean> commentResult = commentService.create(comment);
        assertThat(commentResult, hasFinishedSuccessfully());
        assertThat(commentResult, hasValueOf(true));
        assertThat(commentResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getCommentById_commentDoesNotExist() {
        when(commentDao.exists(anyLong())).thenReturn(false);
        Result<Comment> commentResult = commentService.getById(TestUtil.ID_ONE);
        assertThat(commentResult, hasFailed());
        assertThat(commentResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getCommentById_commentExists() {
        Comment comment = a(comment());
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        Result<Comment> commentResult = commentService.getById(TestUtil.ID_ONE);
        assertThat(commentResult, hasFinishedSuccessfully());
        assertThat(commentResult, hasValueOf(comment));
        assertThat(commentResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetCommentsById_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        Result<List<Comment>> tweetCommentsResult = commentService.getTweetCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFailed());
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getTweetCommentsById_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetId(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        Result<List<Comment>> tweetCommentsResult = commentService.getTweetCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(tweetCommentsResult, hasValueOf(emptyList()));
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getTweetCommentsById_tweetExistsSomeComments() {
        Comment commentOne = a(comment());
        Comment commentTwo = a(comment());

        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetId(anyLong(), any(Pageable.class)))
                .thenReturn(aListWith(
                        commentOne,
                        commentTwo
                ));

        Result<List<Comment>> tweetCommentsResult = commentService.getTweetCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(tweetCommentsResult, hasValueOf(aListWith(commentOne, commentTwo)));
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }


    @Test
    public void getLatestCommentsById_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        Result<List<Comment>> tweetCommentsResult = commentService.getLatestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFailed());
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getLatestCommentsById_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByCreateDateAsc(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        Result<List<Comment>> tweetCommentsResult = commentService.getLatestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(tweetCommentsResult, hasValueOf(emptyList()));
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getLatestCommentsById_tweetExistsSomeCommentsOrderTest() {
        User owner = a(user());
        Comment commentOne = a(comment()
                .withOwner(owner)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Comment commentTwo = a(comment()
                .withOwner(owner)
                .withCreateDate(TestUtil.DATE_2002)
        );
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByCreateDateAsc(anyLong(), any(Pageable.class)))
                .thenReturn(aListWith(
                        commentOne,
                        commentTwo
                ));

        Result<List<Comment>> tweetCommentsResult = commentService.getLatestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(
                tweetCommentsResult, hasValueOf(
                        aListWith(
                                commentOne,
                                commentTwo
                        )
                )
        );
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }


    @Test
    public void getOldestCommentsById_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        Result<List<Comment>> tweetCommentsResult = commentService.getOldestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFailed());
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getOldestCommentsById_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByCreateDateDesc(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        Result<List<Comment>> tweetCommentsResult = commentService.getOldestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(tweetCommentsResult, hasValueOf(emptyList()));
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getOldestCommentsById_tweetExistsSomeCommentsOrderTest() {
        User owner = a(user());
        Comment commentOne = a(comment()
                .withOwner(owner)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Comment commentTwo = a(comment()
                .withOwner(owner)
                .withCreateDate(TestUtil.DATE_2002)
        );
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByCreateDateDesc(anyLong(), any(Pageable.class)))
                .thenReturn(aListWith(
                        commentTwo,
                        commentOne
                ));

        Result<List<Comment>> tweetCommentsResult = commentService.getOldestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(
                tweetCommentsResult, hasValueOf(
                        aListWith(
                                commentTwo,
                                commentOne
                        )
                )
        );
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getMostVotedComments_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        Result<List<Comment>> tweetCommentsResult = commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFailed());
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void getMostVotedComments_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByVotes(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        Result<List<Comment>> tweetCommentsResult = commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(tweetCommentsResult, hasValueOf(emptyList()));
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void getMostVotedComments_tweetExistsSomeCommentsOrderTest() {
        User owner = a(user());
        Comment commentOne = a(comment()
                .withOwner(owner)
                .withNumberOfVotesOf(10L)
        );
        Comment commentTwo = a(comment()
                .withOwner(owner)
                .withNumberOfVotesOf(5L)
        );
        Comment commentThree = a(comment()
                .withOwner(owner)
                .withNumberOfVotesOf(30L)
        );
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByVotes(anyLong(), any(Pageable.class)))
                .thenReturn(aListWith(
                        commentThree,
                        commentOne,
                        commentTwo
                ));

        Result<List<Comment>> tweetCommentsResult = commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, hasFinishedSuccessfully());
        assertThat(
                tweetCommentsResult, hasValueOf(
                        aListWith(
                                commentThree,
                                commentOne,
                                commentTwo
                        )
                )
        );
        assertThat(tweetCommentsResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void vote_userDoesNotExist() {
        when(userDao.exists(anyLong())).thenReturn(false);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(comment())
                )
        );
        Result<Boolean> voteResult = commentService.vote(userVote);
        assertThat(voteResult, hasFailed());
        assertThat(voteResult, hasMessageOf(MessageUtil.USER_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void vote_userExistsPostDoesNot() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(commentDao.exists(anyLong())).thenReturn(false);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(comment())
                )
        );
        Result<Boolean> voteResult = commentService.vote(userVote);
        assertThat(voteResult, hasFailed());
        assertThat(voteResult, hasMessageOf(MessageUtil.POST_DOES_NOT_EXISTS_BY_ID_ERROR_MSG));
    }

    @Test
    public void vote_userAndPostExistsButPostAlreadyVoted() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(commentDao.exists(anyLong())).thenReturn(true);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(comment())
                )
        );
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(userVote);
        Result<Boolean> voteResult = commentService.vote(userVote);
        assertThat(voteResult, hasFailed());
        assertThat(voteResult, hasMessageOf(MessageUtil.POST_ALREADY_VOTED));
    }

    @Test
    public void vote_successVote() {
        when(userDao.exists(anyLong())).thenReturn(true);
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(null);
        UserVote userVote = a(userVote()
                .withUser(
                        a(user())
                )
                .withAbstractPost(
                        a(comment())
                )
        );
        Result<Boolean> voteResult = commentService.vote(userVote);
        assertThat(voteResult, hasFinishedSuccessfully());
        assertThat(voteResult, hasValueOf(true));
        assertThat(voteResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }
}
