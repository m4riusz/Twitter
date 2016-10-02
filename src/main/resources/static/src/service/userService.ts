import {inject} from "aurelia-dependency-injection";
import {HttpClient} from "aurelia-fetch-client";
import {Const} from "../domain/const";
import {BASE_URL, CURRENT_USER, USER_BY_ID, FOLLOW_USER, USER_FOLLOWERS, USER_FOLLOWERS_COUNT} from "../domain/route";
import {BasicService} from "./basicService";
import User = Models.User;
/**
 * Created by mariusz on 02.09.16.
 */


export interface IUserService {
    getCurrentLoggedUser():Promise<User>;
    getUserById(userId:number):Promise<User>;
    isFollowed(userId:number):Promise<boolean>;
    followUser(userId:number):Promise<any>;
    unfollowUser(userId:number):Promise<any>;
    getUserFollowers(userId:number, page:number, size:number):Promise<User[]>;
    getUserFollowersCount(userId:number):Promise<number>;

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

    getUserById(userId:number):Promise<User> {
        return new Promise<User>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_BY_ID(userId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((user:User) => {
                    resolve(user);
                });
        });
    }

    followUser(userId:number):Promise<any> {
        return new Promise<any>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + FOLLOW_USER(userId), {
                method: 'post',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    if (response.ok) {
                        resolve();
                    } else {
                        response.json().then(data => reject(data.message));
                    }
                })
        });
    }

    unfollowUser(userId:number):Promise<any> {
        return new Promise<any>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + FOLLOW_USER(userId), {
                method: 'delete',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    if (response.ok) {
                        resolve();
                    } else {
                        response.json().then(data => reject(data.message));
                    }
                })
        });
    }

    isFollowed(userId:number):Promise<boolean> {
        return new Promise<boolean>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + FOLLOW_USER(userId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(followed => resolve(followed))
        });
    }

    getUserFollowers(userId:number, page:number, size:number):Promise<User[]> {
        return new Promise<User[]>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_FOLLOWERS(userId, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(followers => resolve(followers));
        });
    }

    getUserFollowersCount(userId:number):Promise<number> {
        return new Promise<number>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_FOLLOWERS_COUNT(userId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(count => resolve(count));
        });
    }

}
