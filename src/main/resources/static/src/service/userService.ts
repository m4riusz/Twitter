import {inject} from "aurelia-dependency-injection";
import {HttpClient} from "aurelia-fetch-client";
import {Const} from "../domain/const";
import {BASE_URL, CURRENT_USER} from "../domain/route";
import {BasicService} from "./basicService";
import User = Models.User;
/**
 * Created by mariusz on 02.09.16.
 */


export interface IUserService {
    getCurrentLoggedUser():Promise<User>;
}

@inject(HttpClient)
export class UserService extends BasicService implements IUserService {

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getCurrentLoggedUser():Promise<User> {
        return new Promise<User>((resolve, reject) => {
            this.httpClient
                .fetch(BASE_URL + CURRENT_USER, {
                    headers: {
                        [Const.TOKEN_HEADER]: this.authToken
                    }
                })
                .then(response => response.json())
                .then(data => resolve(data))
        });
    }
}
