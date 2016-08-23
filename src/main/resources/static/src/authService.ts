import {Const} from "./const";
import {Aurelia} from "aurelia-framework";
import {HttpClient} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
/**
 * Created by mariusz on 23.08.16.
 */


@inject(Aurelia, HttpClient)
export class AuthService {

    private token:string;
    private aurelia:Aurelia;
    private httpClient:HttpClient;

    constructor(aurelia:Aurelia, httpClient:HttpClient) {
        this.httpClient = httpClient;
        this.aurelia = aurelia;

        this.token = localStorage[Const.TOKEN_HEADER];
    }

    public login(username:string, password:string):void {
        this.httpClient
            .fetch(Const.BASE_URL + Const.LOGIN_URL, {
                method: 'post',
                headers: {
                    Username: username,
                    Password: password
                }
            })
            .then((response) => {
                let statusCode = response.status;
                let authToken = response.headers.get(Const.TOKEN_HEADER);
                if (statusCode == 200) {
                    localStorage[Const.TOKEN_HEADER] = authToken;
                    this.token = authToken;
                    this.aurelia.setRoot(Const.APP_ROOT);
                }
            })
    }


    public isAuthenticated():boolean {
        return !(this.token == null);
    }

}