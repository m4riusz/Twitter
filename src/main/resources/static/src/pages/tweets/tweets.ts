import User = Models.User;
import Tweet = Models.Tweet;
import {ITweetService, TweetService} from "../../service/tweetService";
import {inject} from "aurelia-dependency-injection";
import {RouteConfig} from "aurelia-router";
import {Const} from "../../domain/const";
/**
 * Created by mariusz on 31.08.16.
 */
@inject(TweetService)
export class Tweets{
    currentLoggedUser:User;
    page:number;
    tweets:Tweet[];
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService) {
        this.page = 0;
        this.tweetService = tweetService;
    }

    async activate(params, routeConfig:RouteConfig) {
        this.currentLoggedUser = routeConfig.settings.currentUser;
        this.tweets = await this.tweetService.getTweetsFromFollowingUsers(this.currentLoggedUser.id, this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getTweetsFromFollowingUsers(this.currentLoggedUser.id, this.page, Const.PAGE_SIZE);
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }
    
}