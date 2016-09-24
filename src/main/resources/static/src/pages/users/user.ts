import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
/**
 * Created by mariusz on 24.09.16.
 */

@inject(UserService)
export class User {
    user:Models.User;
    currentLoggedUser:Models.User;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.userService = userService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.user = await this.userService.getUserById(params.userId);
        console.log(this.user);
        console.log(this.currentLoggedUser);
    }
}