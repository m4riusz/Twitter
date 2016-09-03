import Tweet = Twitter.Models.Tweet;
import UserVote = Twitter.Models.UserVote;
import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BASE_URL, TWEET_URL, TWEET_GET_ALL} from "./route";
import {Const} from "./const";
/**
 * Created by mariusz on 01.09.16.
 */


export interface TweetService {
    create(tweet:Tweet):Promise<Tweet>;
    getAllTweets(page:number, size:number):Promise<Tweet[]>;
}

@inject(HttpClient)
export class TweetServiceImpl implements TweetService {

    private httpClient:HttpClient;
    private authToken:string;

    constructor(httpClient:HttpClient) {
        this.httpClient = httpClient;
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    create(tweet:Tweet):Promise<Tweet> {
        return new Promise((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_URL, {
                headers: {
                    method: 'post',
                    [Const.TOKEN_HEADER]: this.authToken,
                    body: json(tweet)
                }
            })
                .then(response => response.json())
                .then(data => {
                    resolve(data);
                })
                .catch(error => {
                    reject(error);
                })
        });
    }

    getAllTweets(page:number, size:number):Promise<Tweet[]> {
        return new Promise((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_GET_ALL(page, size), {
                headers: {
                    method: 'get',
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(data => {
                    resolve(data);
                })
                .catch(error => {
                    reject(error);
                })
        });
    }


}