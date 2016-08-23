import {AuthService} from "./authService";
import {inject} from "aurelia-dependency-injection";
/**
 * Created by mariusz on 23.08.16.
 */

@inject(AuthService)
export class Login {

    authService:AuthService;
    username:string;
    password:string;

    constructor(authService:AuthService) {
        this.username = "";
        this.password = "";
        this.authService = authService;
    }


    public login(username:string, password:string):void {
        if (this.username && this.password) {
            this.authService.login(this.username, this.password)
        } else {
            alert('Please enter a username and password.');
        }
    }
}