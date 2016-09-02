import {inject} from "aurelia-dependency-injection";
import {HttpClient} from "aurelia-fetch-client";
import {BASE_URL, CURRENT_USER} from "./route";
import {Const} from "./const";
/**
 * Created by mariusz on 02.09.16.
 */


export interface UserService {
    getCurrentLoggedUser():Promise<User>;
}

@inject(HttpClient)
export class UserServiceImpl implements UserService {

    private httpClient:HttpClient;
    private authToken:string;

    constructor(httpClient:HttpClient) {
        this.httpClient = httpClient;
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getCurrentLoggedUser():Promise<User> {
        return this.httpClient
            .fetch(BASE_URL + CURRENT_USER, {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
    }

}