import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BASE_URL, LOGIN, REGISTER, CURRENT_USER} from "./route";
import Response = Aurelia.Response;

/**
 * Created by mariusz on 23.08.16.
 */

export interface IAuthService {
    isTokenValid(token:string):Promise<boolean>;
    login(username:string, password:string):Promise<Response>;
    register(username:string, password:string, email:string, gender:Models.Gender):Promise<Response>;
}

@inject(HttpClient)
export class AuthService implements IAuthService {
    private httpClient:HttpClient;

    constructor(httpClient:HttpClient) {
        this.httpClient = httpClient;
    }

    public login(username:string, password:string):Promise<Response> {
        return this.httpClient
            .fetch(BASE_URL + LOGIN, {
                method: 'post',
                headers: {
                    Username: username,
                    Password: password
                }
            });
    }

    public register(username:string, password:string, email:string, gender:Models.Gender):Promise<Response> {
        return this.httpClient
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
    }

    public isTokenValid(token:string):Promise<boolean> {
        return this.httpClient
            .fetch(BASE_URL + CURRENT_USER, {
                headers: {
                    'method': 'get',
                    'Auth-Token': token
                }
            })
            .then(response => response.ok);
    }
}
