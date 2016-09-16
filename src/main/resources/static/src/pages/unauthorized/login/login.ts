import {inject} from "aurelia-dependency-injection";
import {Aurelia} from "aurelia-framework";
import {AuthService, IAuthService} from "../../../service/authService";
import {Const} from "../../../domain/const";

/**
 * Created by mariusz on 23.08.16.
 */

@inject(AuthService, Aurelia)
export class Login {
    private authService:IAuthService;
    private aurelia:Aurelia;
    private token:string;

    username:string;
    password:string;
    error:string;

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

    public login():void {
        this.authService.login(this.username, this.password)
            .then(response => {
                let authToken = response.headers.get(Const.TOKEN_HEADER);
                if (response.ok) {
                    localStorage[Const.TOKEN_HEADER] = authToken;
                    this.token = authToken;
                    this.aurelia.setRoot(Const.APP_ROOT);
                    return;
                }
                response.json()
                    .then(data => {
                        this.error = data.message;
                    })
            })
    }

    public isAuthenticated():Promise<boolean> {
        return this.authService.isTokenValid(this.token);
    }

}