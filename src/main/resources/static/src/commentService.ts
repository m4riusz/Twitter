import {HttpClient} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BasicService} from "./basicService";
import {Const} from "./const";
import {BASE_URL, COMMENTS_FROM_TWEET} from "./route";
import Comment = Models.Comment;

/**
 * Created by mariusz on 14.09.16.
 */

export interface ICommentService {
    getTweetComments(tweetId:number, page:number, size:number):Promise<Comment[]>;
}

@inject(HttpClient)
export class CommentService extends BasicService implements ICommentService {

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getTweetComments(tweetId:number, page:number, size:number):Promise<Comment[]> {
        return new Promise<Comment[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + COMMENTS_FROM_TWEET(tweetId, page, size), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response=> response.json())
                .then((comments:Comment[]) => {
                    console.log(comments);
                    resolve(comments);
                }, (error) => resolve([]));
        });
    }
}