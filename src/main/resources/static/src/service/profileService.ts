import Avatar = Models.Avatar;
import {BasicService} from "./basicService";
import {inject} from "aurelia-dependency-injection";
import {HttpClient, json} from "aurelia-fetch-client";
import {BASE_URL, CHANGE_USER_AVATAR} from "../domain/route";
import {Const} from "../domain/const";
/**
 * Created by mariusz on 10.10.16.
 */


export interface IProfileService {
    changeUserAvatar(userId:number, avatar:Avatar):Promise<Avatar>;
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
                    if (response.ok) {
                        response.json().then(data => resolve(data));
                    }
                    else {
                        response.json().then(res=> reject(res.message));
                    }
                })

        });
    }

}