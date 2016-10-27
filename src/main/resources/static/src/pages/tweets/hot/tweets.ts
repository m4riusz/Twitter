import User = Models.User;
import Tweet = Models.Tweet;
import {ITweetService, TweetService} from "../../../service/tweetService";
import {inject} from "aurelia-dependency-injection";
import {RouteConfig} from "aurelia-router";
import {Const} from "../../../domain/const";
/**
 * Created by mariusz on 31.08.16.
 */
@inject(TweetService)
export class Tweets {

    currentLoggedUser:User;
    page:number;
    tweets:Tweet[];
    hours:number;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService) {
        this.hours = 6;
        this.page = 0;
        this.tweetService = tweetService;
    }

    async activate(params, routeConfig:RouteConfig) {
        this.hours = params.hours;
        this.currentLoggedUser = routeConfig.settings.currentUser;
        this.tweets = await this.tweetService.getMostPopularTweets(this.hours, this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getMostPopularTweets(this.hours, this.page, Const.PAGE_SIZE);
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}