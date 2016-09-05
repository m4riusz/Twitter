import {RouterConfiguration, Router} from "aurelia-router";
import {UserService, UserServiceImpl} from "./userService";
import {inject} from "aurelia-dependency-injection";
import User = Twitter.Models.User;

/**
 * Created by mariusz on 22.08.16.
 */

@inject(UserServiceImpl)
export class App {

    public loggedUser:User;
    public router:Router;
    private userService:UserService;

    constructor(userService:UserService) {
        this.userService = userService;
    }

    async activate() {
        this.loggedUser = await this.userService.getCurrentLoggedUser();
    }

    configureRouter(config:RouterConfiguration, router:Router) {

        config.map([
            {route: ['', 'home'], name: 'home', moduleId: 'home', title: 'Home', nav: true},
            {route: ['tweets'], name: 'tweets', moduleId: 'tweets', title: 'Tweets', nav: true}
        ]);
        this.router = router;
    }
}