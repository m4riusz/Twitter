import {inject} from "aurelia-framework";
import {RouteConfig} from "aurelia-router";
import {TweetService, ITweetService} from "../../service/tweetService";
import {Const} from "../../domain/const";
import Tweet = Models.Tweet;
import User = Models.User;
import Report = Models.Report;

/**
 * Created by mariusz on 27.10.16.
 */

@inject(TweetService)
export class FavouriteTweets {
    
    configureRouter(){
        
    }
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
        this.tweets = await this.tweetService.getFavouriteTweetsFrom(this.currentLoggedUser.id, this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getFavouriteTweetsFrom(this.currentLoggedUser.id, this.page, Const.PAGE_SIZE);
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}
