import User = Models.User;
import {inject} from "aurelia-framework";
import {UserService, IUserService} from "../../service/userService";
import {Const} from "../../domain/const";
/**
 * Created by mariusz on 02.10.16.
 */

@inject(UserService)
export class UserFollowing {
    currentLoggedUser:User;
    userId:number;
    following:User[];
    followingCount:number;
    private page:number;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.page = 0;
        this.userService = userService;
    }

    async activate(params, config) {
        this.userId = params.userId;
        this.currentLoggedUser = config.settings.currentUser;
        [this.following, this.followingCount] = await Promise.all([
            this.userService.getUserFollowingUsers(this.userId, this.page, Const.PAGE_SIZE),
            this.userService.getUserFollowingUsersCount(this.userId)
        ]);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextFollowingUsersPage = await this.userService.getUserFollowingUsers(this.userId, this.page, Const.PAGE_SIZE);
            this.following = this.following.concat(nextFollowingUsersPage);
        } catch (error) {
            this.page = --this.page;
        }
    }
}