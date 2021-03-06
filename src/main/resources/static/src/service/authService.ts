import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BASE_URL, LOGIN, REGISTER, CURRENT_USER, LOGOUT, USER_ACTIVATE_ACCOUNT} from "../domain/route";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import VerifyResult = Models.VerifyResult;

/**
 * Created by mariusz on 23.08.16.
 */

export interface IAuthService {
    isTokenValid(token: string): Promise<boolean>;
    login(username: string, password: string): Promise<string>;
    logout(): Promise<any>;
    register(username: string, password: string, email: string, gender: string): Promise<string>;
    verify(verifyKey: string): Promise<VerifyResult>;
}

@inject(HttpClient)
export class AuthService extends BasicService implements IAuthService {

    private authToken: string;

    constructor(httpClient: HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    public login(username: string, password: string): Promise<string> {
        return new Promise<string>((resolve, reject) => {
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
                        this.authToken = authToken;
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

    public logout(): Promise<any> {
        return new Promise<string>((resolve, reject) => {
            this.httpClient
                .fetch(BASE_URL + LOGOUT, {
                    method: 'post',
                    headers: {
                        [Const.TOKEN_HEADER]: this.authToken
                    }
                })
                .then(response => {
                    if (response.ok) {
                        resolve("You have logged out!");
                    } else {
                        response.json()
                            .then(data => {
                                reject(data.message);
                            })
                    }
                })
        });
    }

    public register(username: string, password: string, email: string, gender: string): Promise<string> {
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

    public isTokenValid(token: string): Promise<boolean> {
        return this.httpClient
            .fetch(BASE_URL + CURRENT_USER, {
                headers: {
                    'method': 'get',
                    [Const.TOKEN_HEADER]: token
                }
            })
            .then(response => response.ok);
    }

    public verify(verifyKey: string): Promise<VerifyResult> {
        return new Promise<VerifyResult>((resolve, reject) => {
            this.httpClient
                .fetch(BASE_URL + USER_ACTIVATE_ACCOUNT(verifyKey), {
                    method: 'get'
                })
                .then(response => {
                    if (response.ok) {
                        response.json().then(result => resolve(result));
                    } else {
                        response.json().then(result => reject(result.message));
                    }
                })

        })

    }
}

