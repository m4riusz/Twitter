import User = Models.User;
import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
import {Const} from "../../domain/const";
/**
 * Created by mariusz on 02.10.16.
 */

@inject(UserService)
export class UserFollowers {
    currentLoggedUser:User;
    userId:number;
    followers:User[];
    followersCount:number;
    private page:number;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.page = 0;
        this.userService = userService;
    }

    async activate(params, config) {
        this.userId = params.userId;
        this.currentLoggedUser = config.settings.currentUser;
        [this.followers, this.followersCount] = await Promise.all(
            [
                this.userService.getUserFollowers(this.userId, this.page, Const.PAGE_SIZE),
                this.userService.getUserFollowersCount(this.userId)
            ]
        )
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextFollowersPage = await this.userService.getUserFollowers(this.userId, this.page, Const.PAGE_SIZE);
            this.followers = this.followers.concat(nextFollowersPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}