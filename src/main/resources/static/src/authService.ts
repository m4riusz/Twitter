import {Const} from "./const";
import {Aurelia} from "aurelia-framework";
import {HttpClient} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BASE_URL, LOGIN, REGISTER, CURRENT_USER} from "./route";

/**
 * Created by mariusz on 23.08.16.
 */


export interface AuthService {
    login(username:string, password:string);
    register(username:string, password:string, email:string, gender:Twitter.Models.Gender);
}

@inject(Aurelia, HttpClient)
export class AuthServiceImpl implements AuthService {

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
            .fetch(BASE_URL + LOGIN, {
                method: 'post',
                headers: {
                    Username: username,
                    Password: password
                }
            })
            .then(response => {
                let authToken = response.headers.get(Const.TOKEN_HEADER);
                let statusCode = response.status;
                if (this.isStatusCodeOk(statusCode)) {
                    localStorage[Const.TOKEN_HEADER] = authToken;
                    this.token = authToken;
                    this.aurelia.setRoot(Const.APP_ROOT);
                    return;
                }
                response.json().then(data => {
                    alert(data.message);
                });
            });
    }

    register(username:string, password:string, email:string, gender:Twitter.Models.Gender) {
        this.httpClient
            .fetch(BASE_URL + REGISTER, {
                method: 'post',
                headers: {
                    "Content-Type": "application/json"
                },
                body: json({
                    "username": username,
                    "password": password,
                    "email": email,
                    "gender": gender
                })
            })
            .then(response => response.json())
            .then(data => {
                let statusCode = data.status;
                if (this.isStatusCodeOk(statusCode)) {
                    alert('Account has been created!');
                } else {
                    alert(data.message);
                }
            });

    }

    public isAuthenticated():boolean {
        return this.token != null && this.isTokenNotExpired();
    }

    private isStatusCodeOk(statusCode:number | string):boolean {
        return statusCode >= 200 && statusCode < 300;
    }

    private isTokenNotExpired() {
        this.httpClient
            .fetch(BASE_URL + CURRENT_USER, {
                'method': 'get',
                'Auth-Token': this.token
            })
            .then(response => {
                return this.isStatusCodeOk(response.status);
            });
    }

}