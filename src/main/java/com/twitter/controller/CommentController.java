package com.twitter.controller;

import com.twitter.dto.PostVote;
import com.twitter.model.Comment;
import com.twitter.model.UserVote;
import com.twitter.route.Route;
import com.twitter.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) {
        return new ResponseEntity<>(commentService.create(comment), HttpStatus.CREATED);
    }

    @RequestMapping(value = Route.COMMENT_BY_ID, method = RequestMethod.DELETE)
    public ResponseEntity deleteComment(@PathVariable long commentId) {
        commentService.delete(commentId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENT_BY_ID, method = RequestMethod.GET)
    public ResponseEntity<Comment> getCommentById(@PathVariable long commentId) {
        return new ResponseEntity<>(commentService.getById(commentId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENTS_FROM_USER, method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getCommentsFromUserById(@PathVariable long commentId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(commentService.getAllFromUserById(commentId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENT_VOTE, method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<UserVote> voteComment(@RequestBody @Valid PostVote postVote) {
        return new ResponseEntity<>(commentService.vote(postVote), HttpStatus.OK);
    }


    @RequestMapping(value = Route.COMMENT_VOTE_BY_ID, method = RequestMethod.DELETE)
    public ResponseEntity deleteVoteComment(@PathVariable long voteId) {
        commentService.deleteVote(voteId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENT_VOTE_BY_COMMENT_ID, method = RequestMethod.GET)
    public ResponseEntity<UserVote> getUserCommentVote(@PathVariable long commentId) {
        return new ResponseEntity<>(commentService.getPostVote(commentId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENTS_FROM_TWEET, method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getCommentsFromTweetById(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(commentService.getTweetCommentsById(tweetId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENTS_LATEST, method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getLatestCommentsFromTweet(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(commentService.getLatestCommentsById(tweetId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENTS_OLDEST, method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getOldestCommentsFromTweet(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(commentService.getOldestCommentsById(tweetId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.COMMENTS_POPULAR, method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> getMostVotedCommentsFromTweet(@PathVariable long tweetId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(commentService.getMostVotedComments(tweetId, new PageRequest(page, size)), HttpStatus.OK);
    }

}
