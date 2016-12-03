import User = Models.User;
import {RouterConfiguration, Router} from "aurelia-router";
import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-dependency-injection";
/**
 * Created by mariusz on 03.12.16.
 */

@inject(UserService)
export class NotificationsMenu {

    currentLoggedUser:User;
    router:Router;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.userService = userService;
    }

    async configureRouter(config:RouterConfiguration, router:Router) {
        this.currentLoggedUser = await this.userService.getCurrentLoggedUser();
        config.map(
            [
                {
                    route: ['', 'unread'],
                    name: 'Unread',
                    title: 'Unread',
                    moduleId: './unread/notification',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                },
                {
                    route: ['read'],
                    name: 'Read',
                    title: 'Read',
                    moduleId: './read/notification',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                }
            ]
        );
        this.router = router;
    }
}