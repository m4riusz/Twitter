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
    COMMENT_URL
} from "../domain/route";
import Comment = Models.Comment;
import Vote = Models.Vote;
import UserVote = Models.UserVote;

/**
 * Created by mariusz on 14.09.16.
 */

export interface ICommentService {
    deleteComment(tweetId:number):Promise<{}>;
    getTweetComments(tweetId:number, page:number, size:number):Promise<Comment[]>;
    getCurrentUserCommentVote(commentId:number):Promise<Vote>;
    voteComment(commentId:number, vote:Vote):Promise<Vote>;
    deleteCommentVote(commentId:number):Promise<{}>;
    getCommentById(commentId:number):Promise<Comment>;
    commentTweet(comment:Comment):Promise<Comment>;
}

@inject(HttpClient)
export class CommentService extends BasicService implements ICommentService {

    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
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
                        this.getCommentCurrentUserData(comment);
                    });
                    resolve(comments);
                }, (error) => resolve([]));
        });
    }

    getCurrentUserCommentVote(commentId:number):Promise<Vote> {
        return new Promise<Vote>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + COMMENT_VOTE_BY_ID(commentId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:UserVote)=> {
                    resolve(data.vote);
                }, ()=> {
                    resolve("NONE");
                });
        })
    }

    voteComment(commentId:number, vote:Vote):Promise<Vote> {
        return new Promise<Vote>((resolve, reject) => {
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
                    this.getCommentCurrentUserData(comment);
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
                .then(response => {
                    let data = response.json();
                    if (response.ok) {
                        data.then((data:Comment) => {
                            resolve(data)
                        });
                    }
                });
        })
    }

    private getCommentCurrentUserData(comment:Comment) {
        this.getCurrentUserCommentVote(comment.id).then((vote:Vote) => comment.loggedUserVote = vote);
    }
}