import {TweetService, TweetServiceImpl} from "./tweetService";
import {Const} from "./const";
import {inject} from "aurelia-dependency-injection";
import Tweet = Twitter.Models.Tweet;
import Vote = Twitter.Models.Vote;
/**
 * Created by mariusz on 31.08.16.
 */
@inject(TweetServiceImpl)
export class Home{
    pageNumber:number;
    tweets:Tweet[];
    tweetService:TweetService;
    viewModel:Home;
    
    constructor(tweetService:TweetService) {
        this.pageNumber = 0;
        this.tweetService = tweetService;
        this.viewModel = this;
    }

    async activate() {
        this.tweets = await this.tweetService.getAllTweets(this.pageNumber, Const.PAGE_SIZE);
        this.tweets.forEach(tweet => this.getTweetVote(tweet));
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId)
            .then(()=> {
                this.updateTweet(tweetId);
            });
    }

    async getTweetVote(tweet:Tweet) {
        tweet.loggedUserVote = await this.tweetService.getCurrentUserTweetVote(tweet.id);
        this.tweets = this.tweets.map(current => current.id == tweet.id ? current = tweet : current);
    }

    async updateTweet(tweetId:number) {
        let updated = await this.tweetService.getTweetById(tweetId);
        this.tweets = this.tweets.map(tweet => tweet.id == tweetId ? tweet = updated : tweet);
    }

}