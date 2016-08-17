package com.twitter.controller;

import com.twitter.dto.PostVote;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.model.UserVote;
import com.twitter.route.Route;
import com.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariusz on 03.08.16.
 */
@RestController
public class TweetController {

    private TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @RequestMapping(value = Route.TWEET_URL, method = RequestMethod.POST)
    public ResponseEntity<Tweet> createTweet(@Valid @RequestBody Tweet tweet) {
        return new ResponseEntity<>(tweetService.create(tweet), HttpStatus.CREATED);
    }

    @RequestMapping(value = Route.TWEET_BY_ID, method = RequestMethod.GET)
    public ResponseEntity<Tweet> getTweetById(@PathVariable long tweetId) {
        return new ResponseEntity<>(tweetService.getById(tweetId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEET_BY_ID, method = RequestMethod.DELETE)
    public ResponseEntity deleteTweet(@PathVariable long tweetId) {
        tweetService.delete(tweetId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEETS_FROM_USER, method = RequestMethod.GET)
    public ResponseEntity<List<Tweet>> getTweetsFromUser(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(tweetService.getAllFromUserById(userId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEET_VOTE, method = RequestMethod.POST)
    public ResponseEntity<UserVote> voteTweet(@RequestBody @Valid PostVote postVote) {
        return new ResponseEntity<>(tweetService.vote(postVote), HttpStatus.CREATED);
    }

    @RequestMapping(value = Route.TWEET_VOTE, method = RequestMethod.PUT)
    public ResponseEntity<UserVote> changeVoteTweet(@RequestBody @Valid PostVote postVote) {
        return new ResponseEntity<>(tweetService.vote(postVote), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEET_VOTE_BY_ID, method = RequestMethod.DELETE)
    public ResponseEntity deleteVoteTweet(@PathVariable long voteId) {
        tweetService.deleteVote(voteId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEET_GET_ALL, method = RequestMethod.GET)
    public ResponseEntity<List<Tweet>> getAllTweets(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(tweetService.getAllTweets(new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEETS_FROM_FOLLOWINGS_USERS, method = RequestMethod.GET)
    public ResponseEntity<List<Tweet>> getTweetsFromFollowingUsers(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(tweetService.getTweetsFromFollowingUsers(userId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEETS_MOST_VOTED, method = RequestMethod.GET)
    public ResponseEntity<List<Tweet>> getMostVotedTweets(@PathVariable int hours, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(tweetService.getMostVotedTweets(hours, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEETS_WITH_TAGS, method = RequestMethod.GET)
    public ResponseEntity<List<Tweet>> getTweetsByTags(@RequestBody @Valid Tag[] tags, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(tweetService.getTweetsByTagsOrderedByNewest(Arrays.asList(tags), new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEETS_FROM_USER_FAVOURITES, method = RequestMethod.GET)
    public ResponseEntity<List<Tweet>> getFavouriteTweetsByUserId(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(tweetService.getFavouriteTweetsFromUser(userId, new PageRequest(page, size)), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEET_TO_USER_FAVOURITES, method = RequestMethod.POST)
    public ResponseEntity<Tweet> addTweetToUsersFavouriteTweets(@PathVariable long tweetId) {
        return new ResponseEntity<>(tweetService.addTweetToFavourites(tweetId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.TWEET_TO_USER_FAVOURITES, method = RequestMethod.DELETE)
    public ResponseEntity deleteTweetFromUsersFavouriteTweets(@PathVariable long tweetId) {
        tweetService.deleteTweetFromFavourites(tweetId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
