import {inject} from "aurelia-dependency-injection";
import {AuthServiceImpl, AuthService} from "./authService";
import {Const} from "./const";

/**
 * Created by mariusz on 24.08.16.
 */

@inject(AuthServiceImpl)
export class Register {

    authService:AuthService;
    username:string;
    password:string;
    passwordConfirm:string;
    email:string;
    emailConfirm:string;
    gender:Twitter.Models.Gender;

    constructor(authService:AuthService) {
        this.authService = authService;
    }

    register() {
        if (this.password !== this.passwordConfirm) {
            alert('Passwords arent equal!');
        } else if (this.email !== this.emailConfirm) {
            alert('Emails arent equal!');
        } else if (!this.usernameHasValidLength()) {
            alert('Wrong username length!');
        } else if (!this.passwordHasValidLength()) {
            alert('Wrong password length!');
        } else {
            this.authService.register(this.username, this.password, this.email, this.gender);
        }
    }

    public usernameHasValidLength():boolean {
        return this.username !== undefined && this.username.length >= Const.LOGIN_LENGTH.MIN && this.username.length <= Const.LOGIN_LENGTH.MAX;
    }

    public passwordHasValidLength():boolean {
        return this.password !== undefined && this.password.length >= Const.PASSWORD_LENGTH.MIN && this.password.length <= Const.PASSWORD_LENGTH.MAX;
    }

}