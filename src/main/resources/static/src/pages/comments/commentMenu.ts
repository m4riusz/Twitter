import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {UserService, IUserService} from "../../service/userService";
import {Router, RouterConfiguration} from "aurelia-router";
import User = Models.User;
import Tweet = Models.Tweet;
import AbstractPost = Models.AbstractPost;
import Report = Models.Report;

/**
 * Created by mariusz on 14.09.16.
 */

@inject(UserService, TweetService)
export class Comment {

    tweet:Tweet;
    router:Router;
    private currentLoggedUser:User;
    private userService:IUserService;
    private tweetService:ITweetService;
    private tweetId:number;

    constructor(userService:IUserService, tweetService:ITweetService) {
        this.userService = userService;
        this.tweetService = tweetService;
    }

    async activate(params, config) {
        this.tweetId = params.tweetId;
        this.tweet = await this.tweetService.getTweetById(this.tweetId);
    }

    async configureRouter(config:RouterConfiguration, router:Router) {
        this.currentLoggedUser = await this.userService.getCurrentLoggedUser();
        config.map(
            [
                {
                    route: ['', 'latest'],
                    name: 'Latest',
                    title: 'Latest',
                    moduleId: './latest/comments',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                },
                {
                    route: 'oldest',
                    moduleId: './oldest/comments',
                    name: 'Oldest',
                    title: 'Oldest',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                },
                {
                    route: 'popular',
                    name: 'Popular',
                    title: 'Popular',
                    moduleId: './popular/comments',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                }
            ]
        );
        this.router = router;
    }

}