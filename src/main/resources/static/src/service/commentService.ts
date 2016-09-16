import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import {BASE_URL, COMMENTS_FROM_TWEET, COMMENT_VOTE_BY_ID, COMMENT_VOTE} from "../domain/route";
import Comment = Models.Comment;
import Vote = Models.Vote;
import UserVote = Models.UserVote;

/**
 * Created by mariusz on 14.09.16.
 */

export interface ICommentService {
    getTweetComments(tweetId:number, page:number, size:number):Promise<Comment[]>;
    getCurrentUserCommentVote(commentId:number):Promise<Vote>;
    voteComment(commentId:number, vote:Vote):Promise<Vote>;
    deleteCommentVote(commentId:number):Promise<{}>;
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

    private getCommentCurrentUserData(comment:Comment) {
        this.getCurrentUserCommentVote(comment.id).then((vote:Vote) => comment.loggedUserVote = vote);
    }
}