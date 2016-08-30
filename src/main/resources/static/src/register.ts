import {inject} from "aurelia-dependency-injection";
import {AuthServiceImpl, AuthService} from "./authService";

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
        } else {
            this.authService.register(this.username, this.password, this.email, this.gender);
        }
    }

}