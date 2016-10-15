import Avatar = Models.Avatar;
import {BasicService} from "./basicService";
import {inject} from "aurelia-dependency-injection";
import {HttpClient, json} from "aurelia-fetch-client";
import {BASE_URL, CHANGE_USER_AVATAR, USER_CHANGE_PASSWORD, USER_CHANGE_EMAIL} from "../domain/route";
import {Const} from "../domain/const";
import User = Models.User;
/**
 * Created by mariusz on 10.10.16.
 */


export interface IProfileService {
    changeUserAvatar(userId:number, avatar:Avatar):Promise<Avatar>;
    changeUserPassword(userId:number, password:string):Promise<User>;
    changeUserEmail(userId:number, email:string):Promise<User>;

}
@inject(HttpClient)
export class ProfileService extends BasicService implements IProfileService {

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    changeUserAvatar(userId:number, avatar:Avatar):Promise<Avatar> {
        return new Promise<Avatar>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + CHANGE_USER_AVATAR(userId), {
                method: 'put',
                body: json(avatar),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    this.responseResolver(response, resolve, reject);
                })

        });
    }

    changeUserPassword(userId:number, password:string):Promise<User> {
        return new Promise<User>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + USER_CHANGE_PASSWORD(userId), {
                method: 'put',
                body: json({password: password}),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    this.responseResolver(response, resolve, reject);
                })
        });
    }

    changeUserEmail(userId:number, email:string):Promise<User> {
        return new Promise<User>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + USER_CHANGE_EMAIL(userId), {
                method: 'put',
                body: json({email: email}),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    this.responseResolver(response, resolve, reject);
                })
        });
    }

    private responseResolver(response, resolve, reject) {
        if (response.ok) {
            response.json().then(data => resolve(data));
        } else {
            response.json().then(error => {
                if (response.status == 400) {
                    reject(error.errors[0].defaultMessage);
                } else {
                    reject(error.message);
                }
            });
        }
    }

}