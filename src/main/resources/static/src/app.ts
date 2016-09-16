import {IUserService, UserService} from "./service/userService";
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
                moduleId: 'pages/home/home',
                title: 'Home',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['tweets'],
                name: 'tweets',
                moduleId: 'pages/tweets/tweets',
                title: 'Tweets',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['comment/:tweetId'],
                name: 'comment',
                moduleId: 'pages/comments/comment',
                title: 'Tweet comments',
                naw: true,
                settings: {currentUser: this.loggedUser}
            }
        ]);
        this.router = router;
    }
}