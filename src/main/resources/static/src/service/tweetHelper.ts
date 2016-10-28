import {BasicService} from "./basicService";
import {TWEET_VOTE_GET_BY_ID, TWEET_FAVOURITE, BASE_URL, TWEET_VOTE_COUNT} from "../domain/route";
import {HttpClient} from "aurelia-fetch-client";
import {Const} from "../domain/const";
import {inject} from "aurelia-framework";
import Tweet = Models.Tweet;
import UserVote = Models.UserVote;
/**
 * Created by mariusz on 21.10.16.
 */


export interface ITweetHelper {
    getCurrentUserTweetData(tweet:Tweet);
}

@inject(HttpClient)
export class TweetHelper extends BasicService implements ITweetHelper {

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getCurrentUserTweetData(tweet:Tweet) {
        this.getCurrentUserTweetVote(tweet.id).then((vote:'UP'|'DOWN'|'NONE') => tweet.loggedUserVote = vote);
        this.tweetBelongsToUsersFavourites(tweet.id).then((favourite:boolean) => tweet.favourite = favourite);
        this.getTweetVoteCount(tweet.id, "UP").then(data => tweet.upVoteCount = data);
        this.getTweetVoteCount(tweet.id, "DOWN").then(data => tweet.downVoteCount = data);
        this.addTagsInText(tweet);
    }

    private getCurrentUserTweetVote(tweetId:number):Promise<'UP'|'DOWN'|'NONE'> {
        return new Promise<'UP'|'DOWN'|'NONE'>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_VOTE_GET_BY_ID(tweetId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:UserVote)=> {
                    resolve(data.vote);
                }, ()=> {
                    resolve('NONE');
                });
        })
    }

    private tweetBelongsToUsersFavourites(tweetId:number):Promise<boolean> {
        return new Promise<boolean>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_FAVOURITE(tweetId), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((belong:boolean)=> resolve(belong))
        });
    }

    private getTweetVoteCount(tweetId:number, vote:'UP'|'DOWN'):Promise<number> {
        return new Promise<number>((resolve, reject)=> {
            this.httpClient.fetch(TWEET_VOTE_COUNT(tweetId, vote), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data) => resolve(data));
        })
    };

    private addTagsInText(tweet:Tweet) {
        tweet.content = tweet.content.replace(/\#([a-zA-Z0-9]+)/g, "<a class='label label-info' href='#/tags/$1'>#$1</a>");
    }
}