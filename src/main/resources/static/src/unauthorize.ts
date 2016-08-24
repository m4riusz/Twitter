import {RouterConfiguration, Router} from "aurelia-router";
/**
 * Created by mariusz on 24.08.16.
 */

export class Unauthorize {

    private router:Router;

    configureRouter(config:RouterConfiguration, router:Router):void {
        this.router = router;
        config.title = 'Welcome to twitter!';
        config.map(
            [
                {route: ['', 'login'], name: 'login', moduleId: 'login'},
                {route: 'register', name: 'register', moduleId: 'register'}
            ]
        );

    }
}