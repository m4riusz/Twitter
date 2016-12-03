import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import {
    BASE_URL,
    COMMENTS_FROM_TWEET,
    COMMENT_VOTE_BY_ID,
    COMMENT_VOTE,
    COMMENT_BY_ID,
    COMMENT_URL,
    COMMENTS_LATEST_FROM_TWEET,
    COMMENTS_OLDEST_FROM_TWEET,
    COMMENTS_POPULAR_FROM_TWEET
} from "../domain/route";
import {PostHelper, IPostHelper} from "./postHelper";
import Comment = Models.Comment;
import UserVote = Models.UserVote;

/**
 * Created by mariusz on 14.09.16.
 */

export interface ICommentService {
    deleteComment(tweetId:number):Promise<{}>;
    getTweetComments(tweetId:number, page:number, size:number):Promise<Comment[]>;
    voteComment(commentId:number, vote:'UP'|'DOWN'):Promise<'UP'|'DOWN'>;
    deleteCommentVote(commentId:number):Promise<{}>;
    getCommentById(commentId:number):Promise<Comment>;
    getLatestCommentsFromTweet(tweetId:number, page:number, size:number):Promise<Comment[]>;
    getOldestCommentsFromTweet(tweetId:number, page:number, size:number):Promise<Comment[]>;
    getMostPopularCommentsFromTweet(tweetId:number, page:number, size:number):Promise<Comment[]>;
    commentTweet(comment:Comment):Promise<Comment>;
}

@inject(HttpClient, PostHelper)
export class CommentService extends BasicService implements ICommentService {

    private authToken:string;
    private postHelper:IPostHelper;

    constructor(httpClient:HttpClient, postHelper:IPostHelper) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
        this.postHelper = postHelper;
    }

    deleteComment(tweetId:number):Promise<{}> {
        return new Promise<{}>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_BY_ID(tweetId), {
                method: 'delete',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    response.ok ? resolve() : reject();
                })
        });
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
                    comments.forEach(comment => {
                        this.postHelper.getCurrentUserPostData(comment);
                    });
                    resolve(comments);
                }, (error) => resolve([]));
        });
    }

    voteComment(commentId:number, vote:'UP'|'DOWN'):Promise<'UP'|'DOWN'> {
        return new Promise<'UP'|'DOWN'>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_VOTE, {
                method: 'post',
                body: json({'postId': commentId, 'vote': vote}),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((userVote:UserVote) => {
                    resolve(userVote.vote);
                });
        });
    }

    deleteCommentVote(commentId:number):Promise<{}> {
        return new Promise<{}>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_VOTE_BY_ID(commentId), {
                method: 'delete',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(()=> {
                    resolve();
                })
        })
    }

    getCommentById(commentId:number):Promise<Comment> {
        return new Promise<Comment>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_BY_ID(commentId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((comment:Comment) => {
                    this.postHelper.getCurrentUserPostData(comment);
                    resolve(comment);
                })
        })
    }

    commentTweet(comment:Comment):Promise<Comment> {
        return new Promise<Comment>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_URL, {
                method: 'post',
                body: json(comment),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(data => {
                    this.postHelper.getCurrentUserPostData(data);
                    resolve(data);
                })
                .catch(error => {
                    reject(error);
                })
        })
    }

    getLatestCommentsFromTweet(tweetId:number, page:number, size:number):Promise<Models.Comment[]> {
        return new Promise<Comment[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + COMMENTS_LATEST_FROM_TWEET(tweetId, page, size), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response=> response.json())
                .then((comments:Comment[]) => {
                    comments.forEach(comment => {
                        this.postHelper.getCurrentUserPostData(comment);
                    });
                    resolve(comments);
                }, (error) => resolve([]));
        });
    }

    getOldestCommentsFromTweet(tweetId:number, page:number, size:number):Promise<Models.Comment[]> {
        return new Promise<Comment[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + COMMENTS_OLDEST_FROM_TWEET(tweetId, page, size), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response=> response.json())
                .then((comments:Comment[]) => {
                    comments.forEach(comment => {
                        this.postHelper.getCurrentUserPostData(comment);
                    });
                    resolve(comments);
                }, (error) => resolve([]));
        });
    }

    getMostPopularCommentsFromTweet(tweetId:number, page:number, size:number):Promise<Models.Comment[]> {
        return new Promise<Comment[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + COMMENTS_POPULAR_FROM_TWEET(tweetId, page, size), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response=> response.json())
                .then((comments:Comment[]) => {
                    comments.forEach(comment => {
                        this.postHelper.getCurrentUserPostData(comment);
                    });
                    resolve(comments);
                }, (error) => resolve([]));
        });
    }

}