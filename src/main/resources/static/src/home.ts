import {TweetService, TweetServiceImpl} from "./tweetService";
import {Const} from "./const";
import {inject} from "aurelia-dependency-injection";
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

    activate() {
        this.tweetService.getAllTweets(this.pageNumber, Const.PAGE_SIZE)
            .then(data => {
                this.tweets = data;
            })
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId)
            .then(() => {
                //todo refresh deleted item
            });
    }
}