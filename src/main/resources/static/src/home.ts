import {TweetService, TweetServiceImpl} from "./tweetService";
import {Const} from "./const";
import {inject} from "aurelia-dependency-injection";
import {Router, RouteConfig} from "aurelia-router";
import Tweet = Twitter.Models.Tweet;
import Vote = Twitter.Models.Vote;
import User = Twitter.Models.User;
/**
 * Created by mariusz on 31.08.16.
 */
@inject(TweetServiceImpl)
export class Home{
    currentLoggedUser:User;
    pageNumber:number;
    tweets:Tweet[];
    tweetService:TweetService;
    router:Router;
    viewModel:Home;

    constructor(tweetService:TweetService) {
        this.pageNumber = 0;
        this.tweetService = tweetService;
        this.viewModel = this;
    }

    async activate(params, routeConfig:RouteConfig) {
        this.currentLoggedUser = routeConfig.settings.currentUser;
        this.tweets = await this.tweetService.getAllTweets(this.pageNumber, Const.PAGE_SIZE);
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId)
            .then(()=> {
                this.updateTweet(tweetId);
            });
    }

    voteOnTweet(tweetId:number, vote:Vote) {
        this.tweetService.voteTweet(tweetId, vote).then((vote)=> this.updateTweetVote(tweetId, vote));
    }

    deleteTweetVote(tweetId:number) {
        this.tweetService.deleteVote(tweetId).then(()=> this.updateTweetVote(tweetId, 'NONE'));
    }


    private async updateTweet(tweetId:number) {
        let updated = await this.tweetService.getTweetById(tweetId);
        this.tweets = this.tweets.map(tweet => tweet.id == tweetId ? tweet = updated : tweet);
    }

    private updateTweetVote(tweetId:number, vote:Vote) {
        this.tweets = this.tweets.map(current => {
            if (current.id == tweetId) {
                current.loggedUserVote = vote;
            }
            return current
        });
    }

}