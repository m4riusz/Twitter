import {RouterConfiguration, Router} from "aurelia-router";
import {IUserService, UserService} from "../../service/userService";
import {inject} from "aurelia-dependency-injection";
import User = Models.User;
/**
 * Created by mariusz on 31.08.16.
 */
@inject(UserService)
export class Tweets{

    router:Router;
    private currentLoggedUser:User;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.userService = userService;
    }

    async configureRouter(config:RouterConfiguration, router:Router) {
        this.currentLoggedUser = await this.userService.getCurrentLoggedUser();
        config.map(
            [
                {
                    route: ['', 'all'],
                    name: 'All',
                    title: 'All',
                    moduleId: './all/tweets',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                },
                {
                    route: 'favourite',
                    moduleId: './favourites/tweets',
                    name: 'My favourite tweets',
                    title: 'Favourite tweets',
                    nav: true,
                    settings: {currentUser: this.currentLoggedUser}
                },
                {
                    route: 'hot',
                    name: 'Hot',
                    title: 'Hot',
                    moduleId: './hot/hotMenu',
                    nav: true
                }
            ]
        );
        this.router = router;
    }

}