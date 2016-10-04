import {inject, Aurelia} from "aurelia-framework";
import {AuthService, IAuthService} from "../../../service/authService";
import {Const} from "../../../domain/const";

/**
 * Created by mariusz on 23.08.16.
 */

@inject(AuthService, Aurelia)
export class Login {
    username:string;
    password:string;
    error:string;
    private authService:IAuthService;
    private aurelia:Aurelia;
    private token:string;

    constructor(authService:IAuthService, aurelia:Aurelia) {
        this.authService = authService;
        this.aurelia = aurelia;
        this.token = localStorage[Const.TOKEN_HEADER];
    }

    activate() {
        this.username = 'mariusz';
        this.password = 'not2you';
        this.error = '';
    }

    public async login() {
        try {
            this.token = await this.authService.login(this.username, this.password);
            localStorage[Const.TOKEN_HEADER] = this.token;
            this.aurelia.setRoot(Const.APP_ROOT);
        } catch (error) {
            this.error = error;
        }
    }

    public isAuthenticated():Promise<boolean> {
        return this.authService.isTokenValid(this.token);
    }

}