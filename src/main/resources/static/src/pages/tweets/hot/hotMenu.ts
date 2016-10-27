import User = Models.User;
import Tweet = Models.Tweet;
import {RouterConfiguration, Router} from "aurelia-router";
import {inject} from "aurelia-dependency-injection";
import {UserService, IUserService} from "../../../service/userService";
/**
 * Created by mariusz on 31.08.16.
 */
@inject(UserService)
export class HotMenuVM {

    router:Router;
    currentLoggedUser:User;
    private userService:IUserService;

    constructor(userService:IUserService) {
        this.userService = userService;
    }

    async configureRouter(config:RouterConfiguration, router:Router) {
        this.currentLoggedUser = await this.userService.getCurrentLoggedUser();
        config.map(
            [
                {route: [''], redirect: '6'},
                {
                    route: [':hours'],
                    name: 'Hot last',
                    moduleId: './tweets',
                    nav: false,
                    settings: {currentUser: this.currentLoggedUser}
                }
            ]
        );
        this.router = router;
    }
}