package com.twitter.controller;

import com.twitter.model.Result;
import com.twitter.model.Tag;
import com.twitter.model.Tweet;
import com.twitter.route.Route;
import com.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    public Result<Boolean> createTweet(@Valid @RequestBody Tweet tweet) {
        return tweetService.createTweet(tweet);
    }

    @RequestMapping(value = Route.TWEET_BY_ID, method = RequestMethod.GET)
    public Result<Tweet> getTweetById(@PathVariable long tweetId) {
        return tweetService.getTweetById(tweetId);
    }

    @RequestMapping(value = Route.TWEET_BY_ID, method = RequestMethod.DELETE)
    public Result<Boolean> deleteTweet(@PathVariable long tweetId) {
        return tweetService.deleteTweetById(tweetId);
    }

    @RequestMapping(value = Route.TWEET_GET_ALL, method = RequestMethod.GET)
    public Result<List<Tweet>> getAllTweets(@PathVariable int page, @PathVariable int size) {
        return tweetService.getAllTweets(new PageRequest(page, size));
    }

    @RequestMapping(value = Route.TWEETS_FROM_FOLLOWINGS_USERS, method = RequestMethod.GET)
    public Result<List<Tweet>> getTweetsFromFollowingUsers(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return tweetService.getTweetsFromFollowingUsers(userId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.TWEETS_FROM_USER, method = RequestMethod.GET)
    public Result<List<Tweet>> getTweetsFromUser(@PathVariable long userId, @PathVariable int page, @PathVariable int size) {
        return tweetService.getTweetsFromUser(userId, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.TWEETS_MOST_VOTED, method = RequestMethod.GET)
    public Result<List<Tweet>> getMostVotedTweets(@PathVariable int hours, @PathVariable int page, @PathVariable int size) {
        return tweetService.getMostVotedTweets(hours, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.TWEETS_WITH_TAGS, method = RequestMethod.GET)
    public Result<List<Tweet>> getTweetsByTags(@RequestBody Tag[] tags, @PathVariable int page, @PathVariable int size) {
        return tweetService.getTweetsByTagsOrderedByNewest(Arrays.asList(tags), new PageRequest(page, size));
    }

}
