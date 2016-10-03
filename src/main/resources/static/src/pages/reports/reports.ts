import {RouterConfiguration, Router} from "aurelia-router";
import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-framework";
import User = Models.User;
import Role = Models.Role;
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
        const routes = [
            {
                route: ['', 'list'],
                title: 'My reports',
                moduleId: './reportList/reportList',
                nav: true,
                settings: {currentUser: this.currentLoggedUser},
                roles: ['USER', 'MOD', 'ADMIN']
            },
            {
                route: 'court',
                title: 'Court',
                moduleId: './court/court',
                nav: true,
                settings: {currentUser: this.currentLoggedUser},
                roles: ['MOD', 'ADMIN']
            }
        ];
        routes.filter(route => route.roles.indexOf(this.currentLoggedUser.role) != -1).forEach(valid => config.map(valid));
        this.router = router;
    }
}