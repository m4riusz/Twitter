import Tag = Models.Tag;
import {inject} from "aurelia-dependency-injection";
import {HttpClient} from "aurelia-fetch-client";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import {BASE_URL, USER_FAVOURITE_TAGS} from "../domain/route";
/**
 * Created by mariusz on 22.10.16.
 */


export interface ITagService {
    getUserFavouriteTags(userId:number):Promise<Tag[]>;
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
                .then(data => resolve(data))
        });
    }

}