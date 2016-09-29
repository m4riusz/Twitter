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
    private router:Router;
    private userService:IUserService;
    private tweetService:ITweetService;

    constructor(userService:IUserService, tweetService:ITweetService) {
        this.userTweetsPage = 0;
        this.userService = userService;
        this.tweetService = tweetService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        [this.user, this.userTweets] = await Promise.all([
            this.userService.getUserById(params.userId),
            this.tweetService.getTweetsFromUser(params.userId, this.userTweetsPage, Const.PAGE_SIZE)]
        );
    }

    configureRouter(config:RouterConfiguration, router:Router):void {
        this.router = router;
        config.map([]);

    }

}