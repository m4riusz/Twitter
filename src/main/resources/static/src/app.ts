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
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['users/:userId'],
                name: 'users',
                moduleId: 'pages/users/user',
                title: 'User',
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['reports'],
                moduleId: 'pages/reports/reports',
                title: ' Reports',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: 'profile',
                moduleId: 'pages/users/profile/profile',
                title: 'Profile',
                nav: false,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: 'tags/:tagNames',
                moduleId: 'pages/tags/tag',
                title: 'Tag',
                nav: false,
                settings: {currentUser: this.loggedUser}
            }
        ]);
        this.router = router;
    }
}