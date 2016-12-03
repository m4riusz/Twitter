import {BasicService} from "./basicService";
import {
    TWEET_VOTE_GET_BY_ID,
    TWEET_FAVOURITE,
    BASE_URL,
    TWEET_VOTE_COUNT,
    COMMENT_VOTE_COUNT,
    COMMENT_VOTE_BY_ID
} from "../domain/route";
import {HttpClient} from "aurelia-fetch-client";
import {Const} from "../domain/const";
import {inject} from "aurelia-framework";
import Tweet = Models.Tweet;
import UserVote = Models.UserVote;
import AbstractPost = Models.AbstractPost;
import Comment = Models.Comment;
/**
 * Created by mariusz on 21.10.16.
 */


export interface IPostHelper {
    getCurrentUserPostData(abstractPost:AbstractPost);
}

@inject(HttpClient)
export class PostHelper extends BasicService implements IPostHelper {
 

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getCurrentUserPostData(abstractPost:AbstractPost) {
        if (abstractPost.type == 'tweet') {
            this.getCurrentUserTweetData(<Tweet>abstractPost);
        } else if (abstractPost.type == 'comment') {
            this.getCurrentUserCommentData(<Comment>abstractPost);
        } else {
            console.warn(`Unknown type of post! ${abstractPost.type}`);
        }
        this.addTagsInText(abstractPost);
    }


    private getCurrentUserCommentData(comment:Comment) {
        this.getCurrentUserCommentVote(comment.id).then((vote) => comment.loggedUserVote = vote);
        this.getCommentVoteCount(comment.id, "UP").then(data => comment.upVoteCount = data);
        this.getCommentVoteCount(comment.id, "DOWN").then(data => comment.downVoteCount = data);
    }

    getCommentVoteCount(commentId:number, vote:'UP'|'DOWN'):Promise<number> {
        return new Promise<number>((resolve, reject)=> {
            this.httpClient.fetch(COMMENT_VOTE_COUNT(commentId, vote), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data) => resolve(data));
        })
    };

    getCurrentUserCommentVote(commentId:number):Promise<'UP'|'DOWN'|'NONE'> {
        return new Promise<'UP'|'DOWN'|'NONE'>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_VOTE_BY_ID(commentId), {
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

    private getCurrentUserTweetData(tweet:Tweet) {
        this.getCurrentUserTweetVote(tweet.id).then((vote:'UP'|'DOWN'|'NONE') => tweet.loggedUserVote = vote);
        this.tweetBelongsToUsersFavourites(tweet.id).then((favourite:boolean) => tweet.favourite = favourite);
        this.getTweetVoteCount(tweet.id, "UP").then(data => tweet.upVoteCount = data);
        this.getTweetVoteCount(tweet.id, "DOWN").then(data => tweet.downVoteCount = data);
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

    private addTagsInText(abstractPost:AbstractPost) {
        abstractPost.content = abstractPost.content.replace(/\#([a-zA-Z0-9]+)/g, "<a class='label label-info' href='#/tags/$1'>#$1</a>");
    }
}