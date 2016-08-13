package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.CommentDao;
import com.twitter.dao.UserVoteDao;
import com.twitter.exception.PostNotFoundException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.*;
import com.twitter.model.dto.PostVote;
import com.twitter.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.twitter.builders.CommentBuilder.comment;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 03.08.16.
 */
@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {
    @Mock
    private TweetService tweetService;
    @Mock
    private CommentDao commentDao;
    @Mock
    private UserService userService;
    @Mock
    private UserVoteDao userVoteDao;

    private CommentService commentService;

    @Before
    public void setUp() {
        commentService = new CommentServiceImpl(commentDao, tweetService, userVoteDao, userService);
    }

    @Test
    public void createComment_test() {
        Comment comment = a(comment());
        when(commentDao.save(any(Comment.class))).thenReturn(comment);
        Comment savedComment = commentService.create(comment);
        assertThat(savedComment, is(comment));
    }

    @Test(expected = PostNotFoundException.class)
    public void getCommentById_commentDoesNotExist() {
        when(commentDao.exists(anyLong())).thenReturn(false);
        commentService.getById(TestUtil.ID_ONE);
    }

    @Test
    public void getCommentById_commentExists() {
        Comment comment = a(comment());
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        Comment foundComment = commentService.getById(TestUtil.ID_ONE);
        assertThat(foundComment, is(comment));
    }

    @Test(expected = PostNotFoundException.class)
    public void getTweetCommentsById_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        List<Comment> tweetComments = commentService.getTweetCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
    }

    @Test
    public void getTweetCommentsById_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetId(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        List<Comment> tweetComments = commentService.getTweetCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetComments, is(emptyList()));
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

        List<Comment> tweetComments = commentService.getTweetCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetComments, hasItems(commentOne, commentTwo));
    }


    @Test(expected = PostNotFoundException.class)
    public void getLatestCommentsById_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        List<Comment> tweetComments = commentService.getLatestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
    }

    @Test
    public void getLatestCommentsById_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByCreateDateAsc(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        List<Comment> tweetComments = commentService.getLatestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetComments, is(emptyList()));
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

        List<Comment> tweetComments = commentService.getLatestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetComments, contains(commentOne, commentTwo));
    }


    @Test(expected = PostNotFoundException.class)
    public void getOldestCommentsById_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        List<Comment> tweetCommentsResult = commentService.getOldestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
    }

    @Test
    public void getOldestCommentsById_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByCreateDateDesc(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        List<Comment> tweetCommentsResult = commentService.getOldestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, is(emptyList()));
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

        List<Comment> tweetCommentsResult = commentService.getOldestCommentsById(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, contains(commentTwo, commentOne));
    }

    @Test(expected = PostNotFoundException.class)
    public void getMostVotedComments_tweetDoesNotExist() {
        when(tweetService.exists(anyLong())).thenReturn(false);
        List<Comment> tweetCommentsResult = commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
    }

    @Test
    public void getMostVotedComments_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByVotes(anyLong(), any(Pageable.class))).thenReturn(emptyList());

        List<Comment> tweetCommentsResult = commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, is(emptyList()));
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

        List<Comment> tweetCommentsResult = commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(tweetCommentsResult, contains(commentThree, commentOne, commentTwo));
    }

    @Test(expected = UserNotFoundException.class)
    public void vote_userDoesNotExist() {
        when(userService.getCurrentLoggedUser()).thenThrow(UserNotFoundException.class);
        commentService.vote(new PostVote(TestUtil.ID_ONE, Vote.UP));
    }

    @Test(expected = PostNotFoundException.class)
    public void vote_userExistsPostDoesNot() {
        when(userService.getCurrentLoggedUser()).thenReturn(a(user()));
        when(commentDao.exists(anyLong())).thenReturn(false);
        commentService.vote(new PostVote(TestUtil.ID_ONE, Vote.UP));
    }

    @Test
    public void vote_successVote() {
        User user = a(user());
        Comment comment = a(comment());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(null);

        UserVote userVote = a(userVote()
                .withUser(
                        user
                )
                .withAbstractPost(
                        comment
                )
        );
        UserVote vote = commentService.vote(new PostVote(comment.getId(), Vote.UP));
        assertThat(vote, is(userVote));
    }

    @Test
    public void vote_successVoteChange() {
        User user = a(user());
        Comment comment = a(comment());


        UserVote userVote = a(userVote()
                .withVote(
                        Vote.DOWN
                )
                .withUser(
                        user
                )
                .withAbstractPost(
                        comment
                )
        );

        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userVoteDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(userVote);

        UserVote vote = commentService.vote(new PostVote(comment.getId(), Vote.UP));
        assertThat(vote, is(userVote));
        assertThat(vote.getVote(), is(Vote.UP));
    }
}
