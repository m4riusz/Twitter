import {IUserService, UserService} from "../service/userService";
import {RouterConfiguration, Router} from "aurelia-router";
import {inject} from "aurelia-dependency-injection";
import {AuthService, IAuthService} from "../service/authService";
import {TagService, ITagService} from "../service/tagService";
import User = Models.User;

/**
 * Created by mariusz on 22.08.16.
 */

@inject(UserService, TagService, AuthService)
export class App {
    loggedUser:User;
    router:Router;
    private userService:IUserService;
    private authService:IAuthService;
    private tagService:ITagService;

    constructor(userService:IUserService, tagService:ITagService, authService:IAuthService) {
        this.userService = userService;
        this.tagService = tagService;
        this.authService = authService;
    }

    async activate() {
        this.loggedUser = await this.userService.getCurrentLoggedUser();
        this.loggedUser.favouriteTags = await this.tagService.getUserFavouriteTags(this.loggedUser.id);
    }

    configureRouter(config:RouterConfiguration, router:Router) {
        config.map([
            {
                route: ['', 'home'],
                name: 'home',
                moduleId: './home/home',
                title: 'Home',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['tweets'],
                name: 'tweets',
                moduleId: './tweets/tweets',
                title: 'Tweets',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['comment/:tweetId'],
                name: 'comment',
                moduleId: './comments/commentMenu',
                title: 'Tweet comments',
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['users/:userId'],
                name: 'users',
                moduleId: './users/user',
                title: 'User',
                settings: {currentUser: this.loggedUser}
            },
            {
                route: ['reports'],
                moduleId: './reports/reports',
                title: ' Reports',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: 'favourite/tags',
                moduleId: './tags/tag',
                title: 'My favourite tags',
                nav: true,
                settings: {currentUser: this.loggedUser, favourite: true}
            },
            {
                route: 'profile',
                moduleId: './users/profile/profile',
                title: 'Profile',
                nav: false,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: 'tags/:tagNames',
                moduleId: './tags/tag',
                title: 'Tag',
                nav: false,
                settings: {currentUser: this.loggedUser, favourite: false}
            }
        ]);
        this.router = router;
    }

    async logout() {
        try {
            await this.authService.logout();
            window.location.href = "/";
        } catch (error) {
            alert(error);
        }
    }
}