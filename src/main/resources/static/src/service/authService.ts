import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BASE_URL, LOGIN, REGISTER, CURRENT_USER} from "../domain/route";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";

/**
 * Created by mariusz on 23.08.16.
 */

export interface IAuthService {
    isTokenValid(token:string):Promise<boolean>;
    login(username:string, password:string):Promise<string>;
    register(username:string, password:string, email:string, gender:Models.Gender):Promise<string>;
}

@inject(HttpClient)
export class AuthService extends BasicService implements IAuthService {
    
    constructor(httpClient:HttpClient) {
        super(httpClient);
    }

    public login(username:string, password:string):Promise<string> {
        return new Promise<string>((resolve, reject)=> {
            this.httpClient
                .fetch(BASE_URL + LOGIN, {
                    method: 'post',
                    headers: {
                        Username: username,
                        Password: password
                    }
                })
                .then(response => {
                    if (response.ok) {
                        let authToken = response.headers.get(Const.TOKEN_HEADER);
                        resolve(authToken);
                    } else {
                        response.json()
                            .then(data => {
                                reject(data.message);
                            })
                    }
                })
        });
    }

    public register(username:string, password:string, email:string, gender:Models.Gender):Promise<string> {
        return new Promise<string>((resolve, reject) => {
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
                .then(response => {
                        if (response.ok) {
                            resolve('Account has been created');
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
                )
        });
    }

    public isTokenValid(token:string):Promise<boolean> {
        return this.httpClient
            .fetch(BASE_URL + CURRENT_USER, {
                headers: {
                    'method': 'get',
                    [Const.TOKEN_HEADER]: token
                }
            })
            .then(response => response.ok);
    }
}
