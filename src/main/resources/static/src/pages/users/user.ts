import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
/**
 * Created by mariusz on 24.09.16.
 */

@inject(UserService, TweetService)
export class User {
    user:Models.User;
    currentLoggedUser:Models.User;
    private userService:IUserService;
    private tweetService:ITweetService;

    constructor(userService:IUserService, tweetService:ITweetService) {
        this.userService = userService;
        this.tweetService = tweetService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.user = await this.userService.getUserById(params.userId);

    }

}