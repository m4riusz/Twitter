import {RouterConfiguration, Router} from "aurelia-router";
/**
 * Created by mariusz on 22.08.16.
 */

export class App {

    public router:Router;

    configureRouter(config:RouterConfiguration, router:Router) {

        config.map([
            {route: ['', 'home'], name: 'home', moduleId: 'home', title: 'Home', nav: true},
            {route: ['tweets'], name: 'tweets', moduleId: 'tweets', title: 'Tweets', nav: true}
        ]);
        this.router = router;
    }
}