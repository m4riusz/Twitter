package com.twitter.controller;

import com.twitter.model.Result;
import com.twitter.model.Tweet;
import com.twitter.route.Route;
import com.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = Route.TWEET_URL, method = RequestMethod.POST)
    public Result<Boolean> createTweet(@Valid @RequestBody Tweet tweet) {
        return tweetService.createTweet(tweet);
    }

    @RequestMapping(value = Route.TWEET_BY_ID, method = RequestMethod.GET)
    public Result<Tweet> getTweetById(@PathVariable long tweetId) {
        return tweetService.getTweetById(tweetId);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = Route.TWEET_BY_ID, method = RequestMethod.DELETE)
    public Result<Boolean> deleteTweet(@PathVariable long tweetId) {
        return tweetService.deleteTweetById(tweetId);
    }

    @RequestMapping(value = Route.TWEET_GET_ALL, method = RequestMethod.GET)
    public Result<List<Tweet>> getAllUsers(@PathVariable int page, @PathVariable int size) {
        return tweetService.getAllTweets(new PageRequest(page, size));
    }

}
