package com.twitter.controller;

import com.twitter.model.Comment;
import com.twitter.model.Result;
import com.twitter.route.Route;
import com.twitter.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
    public Result<Boolean> createComment(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @RequestMapping(value = Route.COMMENTS_FROM_TWEET, method = RequestMethod.GET)
    public Result<List<Comment>> getCommentsFromTweetById(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return commentService.getTweetCommentsById(tweetId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.COMMENT_BY_ID, method = RequestMethod.GET)
    public Result<Comment> getCommentById(@PathVariable long commentId) {
        return commentService.getCommentById(commentId);
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
