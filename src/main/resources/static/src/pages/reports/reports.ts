import {RouterConfiguration, Router} from "aurelia-router";
import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
import User = Models.User;
/**
 * Created by mariusz on 03.10.16.
 */

@inject(UserService)
export class Reports {
    router:Router;
    currentLoggedUser:User;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.userService = userService;
    }

    async configureRouter(config:RouterConfiguration, router:Router) {
        this.currentLoggedUser = await this.userService.getCurrentLoggedUser();
        config.map([
            {
                route: ['', 'list'],
                title: 'My reports',
                moduleId: './reportList/reportList',
                nav: true,
                settings: {currentUser: this.currentLoggedUser}
            },
            {
                route: 'court',
                title: 'Court',
                moduleId: './court/court',
                nav: this.currentLoggedUser.role != 'USER',
                settings: {currentUser: this.currentLoggedUser}
            }
        ]);
        this.router = router;
    }
}