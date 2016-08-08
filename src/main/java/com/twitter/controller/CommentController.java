package com.twitter.controller;

import com.twitter.model.Comment;
import com.twitter.model.Result;
import com.twitter.model.UserVote;
import com.twitter.route.Route;
import com.twitter.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by mariusz on 05.08.16.
 */
@RestController
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(value = Route.COMMENT_URL, method = RequestMethod.POST)
    public Result<Boolean> createComment(@Valid @RequestBody Comment comment) {
        return commentService.create(comment);
    }

    @RequestMapping(value = Route.COMMENT_BY_ID, method = RequestMethod.DELETE)
    public Result<Boolean> deleteComment(@PathVariable long commentId) {
        return commentService.delete(commentId);
    }

    @RequestMapping(value = Route.COMMENT_BY_ID, method = RequestMethod.GET)
    public Result<Comment> getCommentById(@PathVariable long commentId) {
        return commentService.getById(commentId);
    }

    @RequestMapping(value = Route.COMMENTS_FROM_USER, method = RequestMethod.GET)
    public Result<List<Comment>> getCommentsFromUserById(@PathVariable long commentId, @PathVariable int page, @PathVariable int size) {
        return commentService.getAllFromUserById(commentId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.COMMENT_VOTE, method = RequestMethod.POST)
    public Result<Boolean> voteComment(@RequestBody @Valid UserVote userVote) {
        return commentService.vote(userVote);
    }

    @RequestMapping(value = Route.COMMENT_VOTE_BY_ID, method = RequestMethod.DELETE)
    public Result<Boolean> deleteVoteComment(@PathVariable long voteId) {
        return commentService.deleteVote(voteId);
    }

    @RequestMapping(value = Route.COMMENT_VOTE, method = RequestMethod.PUT)
    public Result<Boolean> changeVoteComment(@RequestBody @Valid UserVote userVote) {
        return commentService.changeVote(userVote);
    }

    @RequestMapping(value = Route.COMMENTS_FROM_TWEET, method = RequestMethod.GET)
    public Result<List<Comment>> getCommentsFromTweetById(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return commentService.getTweetCommentsById(tweetId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.COMMENTS_LATEST, method = RequestMethod.GET)
    public Result<List<Comment>> getLatestCommentsFromTweet(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return commentService.getLatestCommentsById(tweetId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.COMMENTS_OLDEST, method = RequestMethod.GET)
    public Result<List<Comment>> getOldestCommentsFromTweet(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return commentService.getOldestCommentsById(tweetId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.COMMENTS_POPULAR, method = RequestMethod.GET)
    public Result<List<Comment>> getMostVotedCommentsFromTweet(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return commentService.getMostVotedComments(tweetId, new PageRequest(page, size));
    }

}
