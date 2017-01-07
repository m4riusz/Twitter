import {IUserService, UserService} from "../service/userService";
import {RouterConfiguration, Router} from "aurelia-router";
import {inject} from "aurelia-dependency-injection";
import {AuthService, IAuthService} from "../service/authService";
import {TagService, ITagService} from "../service/tagService";
import {NotificationService, INotificationService} from "../service/notificationService";
import {EventAggregator} from "aurelia-event-aggregator";
import {Const} from "../domain/const";
import User = Models.User;
import Notification = Models.Notification;

/**
 * Created by mariusz on 22.08.16.
 */

@inject(UserService, TagService, NotificationService, AuthService, EventAggregator)
export class App {
    loggedUser: User;
    router: Router;
    notifications: Notification[];
    private userService: IUserService;
    private authService: IAuthService;
    private tagService: ITagService;
    private notificationService: INotificationService;
    private eventAggregator: EventAggregator;
    private agregator: any;

    constructor(userService: IUserService, tagService: ITagService, notificationService: INotificationService, authService: IAuthService, eventAggregator: EventAggregator) {
        this.userService = userService;
        this.tagService = tagService;
        this.notificationService = notificationService;
        this.authService = authService;
        this.eventAggregator = eventAggregator;
    }

    async activate() {
        [this.loggedUser, this.notifications] = await Promise.all([
            this.userService.getCurrentLoggedUser(),
            this.notificationService.getLatestNotifications(false, 0, 10)
        ]);
        this.loggedUser.favouriteTags = await this.tagService.getUserFavouriteTags(this.loggedUser.id);
    }

    attached() {
        this.agregator = this.eventAggregator.subscribe(Const.NOTIFICATION_EVENT, notifications => {
            this.notifications = notifications;
        });
    }

    detached() {
        this.agregator.dispose();
    }

    configureRouter(config: RouterConfiguration, router: Router) {
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
            },
            {
                route: 'notifications',
                moduleId: './notifications/menu',
                title: 'Notifications',
                nav: false,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: 'search',
                moduleId: './search/search',
                title: 'Search',
                nav: true,
                settings: {currentUser: this.loggedUser}
            },
            {
                route: 'verify/:key',
                redirect: "/"
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