import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {Const} from "../../domain/const";
import {Router, RouterConfiguration} from "aurelia-router";
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 24.09.16.
 */

@inject(UserService, TweetService)
export class User {
    userTweets:Tweet[];
    userTweetsPage:number;
    user:Models.User;
    currentLoggedUser:Models.User;
    router:Router;
    followed:boolean;
    private userService:IUserService;
    private tweetService:ITweetService;

    constructor(userService:IUserService, tweetService:ITweetService) {
        this.userTweetsPage = 0;
        this.userService = userService;
        this.tweetService = tweetService;
    }

    async activate(params) {
        const userId = params.userId;
        [this.user, this.userTweets, this.followed] = await Promise.all([
            this.userService.getUserById(userId),
            this.tweetService.getTweetsFromUser(userId, this.userTweetsPage, Const.PAGE_SIZE),
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
                name: 'usersTweets',
                moduleId: '../tweets/userTweets',
                nav: true,
                title: 'Tweets from user',
                settings: {currentUser: this.currentLoggedUser}
            }
        ]);
        this.router = router;
    }
}