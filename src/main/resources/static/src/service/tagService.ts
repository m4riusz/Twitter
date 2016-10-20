import Tag = Models.Tag;
import {BasicService} from "./basicService";
import {inject} from "aurelia-dependency-injection";
import {HttpClient} from "aurelia-fetch-client";
import {Const} from "../domain/const";
import {TWEETS_BY_TAGS} from "../domain/route";
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 19.10.16.
 */


export interface ITagService {
    getTweetsByTags(tags:Tag[], page:number, size:number):Promise<Tweet[]>;
}

@inject(HttpClient)
export class TagService extends BasicService implements ITagService {
    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }


    getTweetsByTags(tags:Tag[], page:number, size:number) {
        return new Promise<Tweet[]>((resolve, reject)=> {
            this.httpClient.fetch(TWEETS_BY_TAGS(tags.map(tag => tag.text), page, size), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:Tweet[]) => {
                    resolve(data);
                })
        });
    }

}