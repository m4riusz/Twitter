import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {Router, RouterConfiguration} from "aurelia-router";
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 24.09.16.
 */

@inject(UserService, TweetService)
export class User {
    user:Models.User;
    currentLoggedUser:Models.User;
    router:Router;
    followed:boolean;
    private userService:IUserService;
    private tweetService:ITweetService;

    constructor(userService:IUserService, tweetService:ITweetService) {
        this.userService = userService;
        this.tweetService = tweetService;
    }

    async activate(params) {
        const userId = params.userId;
        [this.user, this.followed] = await Promise.all([
            this.userService.getUserById(userId),
            this.userService.isFollowed(userId)
        ]);
    }

    async followUser(userId:number) {
        try {
            await this.userService.followUser(userId);
            this.followed = true;
        } catch (error) {
            alert(error);
        }
    }

    async unfollowUser(userId:number) {
        try {
            await this.userService.unfollowUser(userId);
            this.followed = false;
        } catch (error) {
            alert(error);
        }
    }

    async configureRouter(config:RouterConfiguration, router:Router) {
        this.currentLoggedUser = await this.userService.getCurrentLoggedUser();
        config.map([
            {
                route: ['', 'tweets'],
                name: 'userTweets',
                moduleId: '../tweets/userTweets',
                nav: true,
                title: 'Tweets from user',
                settings: {currentUser: this.currentLoggedUser}
            },
            {
                route: 'followers',
                name: 'userFollowers',
                moduleId: './userFollowers',
                nav: true,
                title: 'User followers',
                settings: {currentUser: this.currentLoggedUser}
            }
        ]);
        this.router = router;
    }
}