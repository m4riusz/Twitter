import {IUserService, UserService} from "./userService";
import {RouterConfiguration, Router} from "aurelia-router";
import {inject} from "aurelia-dependency-injection";
import User = Models.User;

/**
 * Created by mariusz on 22.08.16.
 */

@inject(UserService)
export class App {

    public loggedUser:User;
    public router:Router;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.userService = userService;
    }

    async activate() {
        this.loggedUser = await this.userService.getCurrentLoggedUser();
    }

    configureRouter(config:RouterConfiguration, router:Router) {

        config.map([
            {
                route: ['', 'home'],
                name: 'home',
                moduleId: 'home',
                title: 'Home',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {route: ['tweets'], name: 'tweets', moduleId: 'tweets', title: 'Tweets', nav: true}
        ]);
        this.router = router;
    }
}