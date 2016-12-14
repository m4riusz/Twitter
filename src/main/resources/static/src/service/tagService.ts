import Tag = Models.Tag;
import {inject} from "aurelia-dependency-injection";
import {HttpClient, json} from "aurelia-fetch-client";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import {BASE_URL, USER_FAVOURITE_TAGS} from "../domain/route";
/**
 * Created by mariusz on 22.10.16.
 */


export interface ITagService {
    getUserFavouriteTags(userId:number):Promise<Tag[]>;
    addTagToFavourites(userId:number, tag:string):Promise<Tag>;
    removeTagFromFavourites(userId:number, tag:string):Promise<any>;
}

@inject(HttpClient)
export class TagService extends BasicService implements ITagService {

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getUserFavouriteTags(userId:number):Promise<Tag[]> {
        return new Promise<Tag[]>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_FAVOURITE_TAGS(userId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response =>response.json())
                .then(data => resolve(data), error => {
                    console.error(error);
                    resolve([]);
                })
        });
    }

    addTagToFavourites(userId:number, tag:string):Promise<Models.Tag> {
        return new Promise<Tag>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_FAVOURITE_TAGS(userId), {
                method: 'post',
                body: json({text: tag}),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    if (response.ok) {
                        response.json().then(tag => resolve(tag));
                    } else {
                        response.json().then(error => reject(error.message));
                    }
                })
        });
    }

    removeTagFromFavourites(userId:number, tag:string):Promise<any> {
        return new Promise<any>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_FAVOURITE_TAGS(userId), {
                method: 'delete',
                body: json({text: tag}),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    if (response.ok) {
                        resolve();
                    } else {
                        response.json().then(error => reject(error.message));
                    }
                })
        });
    }

}