import {TweetService, TweetServiceImpl} from "./tweetService";
import {Const} from "./const";
import {inject} from "aurelia-dependency-injection";
import Tweet = Twitter.Models.Tweet;
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
        this.tweets = await this.tweetService.getAllTweets(this.pageNumber, Const.PAGE_SIZE)
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId)
            .then(()=> {
                this.updateTweet(tweetId);
            });

    }

    async updateTweet(tweetId:number) {
        let updated = await this.tweetService.getTweetById(tweetId);
        this.tweets = this.tweets.map(tweet => tweet.id == tweetId ? tweet = updated : tweet);
    }

}