import Tweet = Twitter.Models.Tweet;
import UserVote = Twitter.Models.UserVote;
import Vote = Twitter.Models.Vote;
import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {
    BASE_URL,
    TWEET_URL,
    TWEET_GET_ALL,
    TWEET_BY_ID,
    TWEET_VOTE_GET_BY_ID,
    TWEET_VOTE,
    TWEET_FAVOURITE,
    USER_FAVOURITE_TWEETS
} from "./route";
import {Const} from "./const";

/**
 * Created by mariusz on 01.09.16.
 */


export interface TweetService {
    create(tweet:Tweet):Promise<Tweet>;
    getAllTweets(page:number, size:number):Promise<Tweet[]>;
    deleteTweet(tweetId:number):Promise<{}>;
    getTweetById(tweetId:number):Promise<Tweet>;
    getCurrentUserTweetVote(tweetId:number):Promise<Vote>;
    voteTweet(tweetId:number, vote:Vote):Promise<Vote>;
    deleteVote(tweetId:number):Promise<{}>;
    addTweetToFavourites(tweetId:number):Promise<Tweet>;
    removeTweetFromFavourites(tweetId:number):Promise<{}>;
    getFavouriteTweetsFrom(userId:number, page:number, size:number):Promise<Tweet[]>;
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
                method: 'post',
                headers: {
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
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:Tweet[]) => {
                    data.forEach(tweet => {
                        this.getCurrentUserTweetVote(tweet.id).then((vote:Vote) => tweet.loggedUserVote = vote);
                    });
                    resolve(data);
                })
                .catch(error => {
                    reject(error);
                })
        });
    }

    deleteTweet(tweetId:number):Promise<{}> {
        return new Promise<{}>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_BY_ID(tweetId), {
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

    getTweetById(tweetId:number):Promise<Tweet> {
        return new Promise((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_BY_ID(tweetId), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:Tweet)=> {
                    this.getCurrentUserTweetVote(tweetId)
                        .then((vote:Vote) => {
                            data.loggedUserVote = vote;
                            resolve(data);
                        });
                })
        })
    }

    getCurrentUserTweetVote(tweetId:number):Promise<Vote> {
        return new Promise((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_VOTE_GET_BY_ID(tweetId), {
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

    voteTweet(tweetId:number, vote:Vote):Promise<Vote> {
        return new Promise<Vote>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_VOTE, {
                method: 'post',
                body: json({'postId': tweetId, 'vote': vote}),
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

    deleteVote(tweetId:number):Promise<{}> {
        return new Promise<{}>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_VOTE_GET_BY_ID(tweetId), {
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

    getFavouriteTweetsFrom(userId:number, page:number, size:number):Promise<Tweet[]> {
        return new Promise<Tweet[]>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_FAVOURITE_TWEETS(userId, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((tweets:Tweet[])=> {
                    resolve(tweets);
                })
        })
    }

    addTweetToFavourites(tweetId:number):Promise<Tweet> {
        return new Promise<Tweet>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_FAVOURITE(tweetId), {
                method: 'post',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((tweet:Tweet)=> {
                    resolve(tweet);
                })
        });
    }

    removeTweetFromFavourites(tweetId:number):Promise<{}> {
        return new Promise<{}>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEET_FAVOURITE(tweetId), {
                method: 'delete',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    resolve();
                })
        })
    }
}