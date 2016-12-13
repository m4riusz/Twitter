import {inject} from "aurelia-dependency-injection";
import {AuthService, IAuthService} from "../../../service/authService";
import {Const} from "../../../domain/const";

/**
 * Created by mariusz on 24.08.16.
 */

@inject(AuthService)
export class Register {
    authService:IAuthService;
    error:string;
    success:string;
    username:string;
    password:string;
    passwordConfirm:string;
    email:string;
    emailConfirm:string;
    gender:string;

    constructor(authService:IAuthService) {
        this.authService = authService;
    }

    activate() {
        this.clearFields();
    }

    public async register() {
        if (this.password !== this.passwordConfirm) {
            this.setErrorMessage('Passwords are not equal!');
        } else if (this.email !== this.emailConfirm) {
            this.setErrorMessage('Emails are not equal!');
        } else if (!this.usernameHasValidLength()) {
            this.setErrorMessage('Wrong username length!');
        } else if (!this.passwordHasValidLength()) {
            this.setErrorMessage('Wrong password length!');
        } else {
            try {
                let response = await this.authService.register(this.username, this.password, this.email, this.gender == null ? "UNDEFINED" : this.gender);
                this.clearFields();
                this.setSuccessMessage(response);
            } catch (error) {
                this.setErrorMessage(error);
            }
        }
    }

    private clearFields():void {
        this.error = '';
        this.success = '';
        this.username = '';
        this.password = '';
        this.passwordConfirm = '';
        this.email = '';
        this.emailConfirm = '';
    }

    private setSuccessMessage(message:string):void {
        this.error = '';
        this.success = message;
    }

    private setErrorMessage(message:string):void {
        this.success = '';
        this.error = message;
    }

    public usernameHasValidLength():boolean {
        return this.username !== undefined && this.username.length >= Const.LOGIN_LENGTH.MIN && this.username.length <= Const.LOGIN_LENGTH.MAX;
    }

    public passwordHasValidLength():boolean {
        return this.password !== undefined && this.password.length >= Const.PASSWORD_LENGTH.MIN && this.password.length <= Const.PASSWORD_LENGTH.MAX;
    }

}