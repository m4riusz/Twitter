package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.CommentDao;
import com.twitter.dto.PostVote;
import com.twitter.exception.PostDeleteException;
import com.twitter.exception.PostNotFoundException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
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
import static com.twitter.builders.PostVoteBuilder.postVote;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.builders.UserVoteBuilder.userVote;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
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
    private UserVoteService userVoteService;

    private CommentService commentService;

    @Before
    public void setUp() {
        commentService = new CommentServiceImpl(commentDao, tweetService, userVoteService, userService);
    }

    @Test
    public void createComment_test() {
        Comment comment = a(comment());
        when(commentDao.save(any(Comment.class))).thenReturn(comment);
        Comment savedComment = commentService.create(comment);
        assertThat(savedComment, is(comment));
    }

    @Test(expected = PostNotFoundException.class)
    public void deleteCommentById_tweetDoesNotExist() {
        when(commentDao.exists(anyLong())).thenReturn(false);
        commentService.delete(TestUtil.ID_ONE);
    }

    @Test(expected = PostDeleteException.class)
    public void deleteCommentById_tweetExistsUserIsNotPostOwner() {
        User user = a(user());
        User otherUser = a(user());
        Comment comment = a(comment()
                .withOwner(user)
        );
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userService.getCurrentLoggedUser()).thenReturn(otherUser);
        commentService.delete(comment.getId());
    }

    @Test(expected = PostDeleteException.class)
    public void deleteCommentById_commentAlreadyDeleted() {
        User user = a(user());
        Comment comment = a(comment()
                .withOwner(user)
                .withDeleted(true)
        );
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        commentService.delete(comment.getId());
    }

    @Test
    public void deleteCommentById_tweetExistsUserIsPostOwner() {
        User user = a(user());
        Comment comment = a(comment()
                .withOwner(user)
                .withDeleted(false)
        );
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        commentService.delete(comment.getId());
        assertThat(comment.getContent(), is(MessageUtil.DELETE_BY_OWNED_ABSTRACT_POST_CONTENT));
        assertThat(comment.isDeleted(), is(true));
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
        commentService.getTweetCommentsById(
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
        commentService.getLatestCommentsById(
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
        when(commentDao.findByTweetIdOrderByCreateDateDesc(anyLong(), any(Pageable.class)))
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
        commentService.getOldestCommentsById(
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
        when(commentDao.findByTweetIdOrderByCreateDateAsc(anyLong(), any(Pageable.class)))
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
        commentService.getMostVotedComments(
                TestUtil.ID_ONE,
                TestUtil.ALL_IN_ONE_PAGE
        );
    }

    @Test
    public void getMostVotedComments_tweetExistsNoComments() {
        when(tweetService.exists(anyLong())).thenReturn(true);
        when(commentDao.findByTweetIdOrderByVotesAscCreateDateDesc(anyLong(), any(Pageable.class))).thenReturn(emptyList());

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
        when(commentDao.findByTweetIdOrderByVotesAscCreateDateDesc(anyLong(), any(Pageable.class)))
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
        commentService.vote(a(postVote()));
    }

    @Test(expected = PostNotFoundException.class)
    public void vote_userExistsPostDoesNot() {
        when(userService.getCurrentLoggedUser()).thenReturn(a(user()));
        when(commentDao.exists(anyLong())).thenReturn(false);
        commentService.vote(a(postVote()));
    }

    @Test
    public void vote_successVoteCreate() {
        User user = a(user());
        Comment comment = a(comment());
        PostVote postVote = a(postVote()
                .withPostId(comment.getId())
                .withVote(Vote.UP)
        );
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(userVoteService.findUserVoteForPost(any(User.class), any(AbstractPost.class))).thenReturn(null);
        when(userVoteService.save(any(UserVote.class))).thenReturn(new UserVote(postVote.getVote(), user, comment));
        UserVote userVote = commentService.vote(
                postVote
        );
        assertThat(userVote.getVote(), is(Vote.UP));
        assertThat(userVote.getAbstractPost(), is(comment));
        assertThat(userVote.getUser(), is(user));
    }

    @Test
    public void vote_successVoteChange() {
        User user = a(user());
        Comment comment = a(comment());
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        when(userService.getCurrentLoggedUser()).thenReturn(user);

        UserVote vote = a(userVote()
                .withUser(user)
                .withAbstractPost(comment)
                .withVote(Vote.UP)
        );
        when(userVoteService.findUserVoteForPost(any(User.class), any(AbstractPost.class))).thenReturn(vote);
        UserVote userVote = commentService.vote(
                a(postVote()
                        .withPostId(comment.getId())
                        .withVote(Vote.DOWN)
                )
        );

        assertThat(userVote.getVote(), is(Vote.DOWN));
        assertThat(userVote.getAbstractPost(), is(comment));
        assertThat(userVote.getUser(), is(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteVote_userDoesNotExist() {
        when(userService.getCurrentLoggedUser()).thenThrow(UserNotFoundException.class);
        commentService.deleteVote(TestUtil.ID_ONE);
    }

    @Test(expected = PostNotFoundException.class)
    public void deleteVote_userExistsPostDoesNot() {
        when(userService.getCurrentLoggedUser()).thenReturn(a(user()));
        when(commentDao.exists(anyLong())).thenReturn(false);
        commentService.deleteVote(TestUtil.ID_ONE);
    }

    @Test
    public void deleteVote_successDeleteVote() {
        User owner = a(user());
        Comment comment = a(comment());
        UserVote userVote = a(userVote()
                .withVote(Vote.UP)
                .withUser(owner)
                .withAbstractPost(comment)
        );
        comment.setVotes(aListWith(userVote));
        comment.setOwner(owner);

        when(userService.getCurrentLoggedUser()).thenReturn(owner);
        when(commentDao.exists(anyLong())).thenReturn(true);
        when(commentDao.findOne(anyLong())).thenReturn(comment);
        commentService.deleteVote(userVote.getId());

        assertThat(comment.getVotes(), not(hasItem(userVote)));
    }

    @Test
    public void getPostVote_postDoesNotExist() {
        User user = a(user());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(commentDao.findOne(anyLong())).thenReturn(null);
        UserVote userVoteForPost = userVoteService.findUserVoteForPost(user, a(comment()));
        assertThat(userVoteForPost, is(nullValue()));
    }

    @Test
    public void getPostVoteCount_postDoesNotExist() {
        long userVoteForPost = userVoteService.getPostVoteCount(TestUtil.ID_ONE, Vote.UP);
        assertThat(userVoteForPost, is(0L));
    }

}
