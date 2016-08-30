import {AuthService, AuthServiceImpl} from "./authService";
import {inject} from "aurelia-dependency-injection";
import {Const} from "./const";

/**
 * Created by mariusz on 23.08.16.
 */

@inject(AuthServiceImpl)
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
        if (this.usernameHasValidLength() && this.passwordHasValidLength()) {
            this.authService.login(this.username, this.password)
        } else {
            alert(`Ensure that username length is between ${Const.LOGIN_LENGTH.MIN} and ${Const.LOGIN_LENGTH.MAX} and
            password length is between ${Const.PASSWORD_LENGTH.MIN} and ${Const.PASSWORD_LENGTH.MAX}`);
        }
    }

    public usernameHasValidLength():boolean {
        return this.username.length >= Const.LOGIN_LENGTH.MIN && this.username.length <= Const.LOGIN_LENGTH.MAX;
    }

    public passwordHasValidLength():boolean {
        return this.password.length >= Const.PASSWORD_LENGTH.MIN && this.password.length <= Const.PASSWORD_LENGTH.MAX;
    }
}