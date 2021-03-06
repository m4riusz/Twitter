import {HttpClient, json} from "aurelia-fetch-client";
import {inject} from "aurelia-dependency-injection";
import {Const} from "../domain/const";
import {
    BASE_URL,
    TWEET_URL,
    TWEET_GET_ALL,
    TWEET_BY_ID,
    TWEET_VOTE_GET_BY_ID,
    USER_FAVOURITE_TWEETS,
    TWEET_FAVOURITE,
    TWEET_VOTE,
    TWEETS_FROM_USER,
    TWEETS_BY_TAGS,
    TWEETS_FROM_FOLLOWING_USERS,
    TWEETS_MOST_VOTED,
    TWEETS_BY_TAGS_POPULAR
} from "../domain/route";
import {BasicService} from "./basicService";
import {PostHelper, IPostHelper} from "./postHelper";
import Tweet = Models.Tweet;
import UserVote =Models.UserVote;
import Tag = Models.Tag;

/**
 * Created by mariusz on 01.09.16.
 */

export interface ITweetService {
    create(tweet:Tweet):Promise<Tweet>;
    getAllTweets(page:number, size:number):Promise<Tweet[]>;
    getTweetsFromFollowingUsers(userId:number, page:number, size:number):Promise<Tweet[]>;
    deleteTweet(tweetId:number):Promise<{}>;
    getTweetById(tweetId:number):Promise<Tweet>;
    voteTweet(tweetId:number, vote:'UP'|'DOWN'):Promise<'UP'|'DOWN'>;
    deleteVote(tweetId:number):Promise<{}>;
    addTweetToFavourites(tweetId:number):Promise<Tweet>;
    removeTweetFromFavourites(tweetId:number):Promise<{}>;
    getFavouriteTweetsFrom(userId:number, page:number, size:number):Promise<Tweet[]>;
    send(tweet:Tweet):Promise<Tweet>;
    getTweetsFromUser(userId:number, page:number, size:number):Promise<Tweet[]>;
    getTweetsByTags(tags:Tag[], page:number, size:number):Promise<Tweet[]>;
    getMostPopularTweets(hours:number, page:number, size:number):Promise<Tweet[]>;
    getMostPopularTweetsWithTags(tags:Tag[],hours:number, page:number, size:number):Promise<Tweet[]>;
}

@inject(HttpClient, PostHelper)
export class TweetService extends BasicService implements ITweetService {

    private authToken:string;
    private postHelper:IPostHelper;

    constructor(httpClient:HttpClient, postHelper:IPostHelper) {
        super(httpClient);
        this.postHelper = postHelper;
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
                    this.postHelper.getCurrentUserPostData(data);
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
                        this.postHelper.getCurrentUserPostData(tweet);
                    });
                    resolve(data);
                })
        });
    }

    getTweetsFromFollowingUsers(userId:number, page:number, size:number):Promise<Tweet[]> {
        return new Promise((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEETS_FROM_FOLLOWING_USERS(userId, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:Tweet[]) => {
                    data.forEach(tweet => {
                        this.postHelper.getCurrentUserPostData(tweet);
                    });
                    resolve(data);
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
                .then((tweet:Tweet)=> {
                    this.postHelper.getCurrentUserPostData(tweet);
                    resolve(tweet);
                })
        })
    }

    voteTweet(tweetId:number, vote:'UP'|'DOWN'):Promise<'UP'|'DOWN'> {
        return new Promise<'UP'|'DOWN'>((resolve, reject) => {
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
                    tweets.forEach(tweet => {
                        this.postHelper.getCurrentUserPostData(tweet);
                    });
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

    send(tweet:Tweet):Promise<Tweet> {
        return new Promise<Tweet>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + TWEET_URL, {
                method: 'post',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                },
                body: json(tweet)
            })
                .then(response => {
                    let data = response.json();
                    if (response.ok) {
                        data.then((data:Tweet) => {
                            this.postHelper.getCurrentUserPostData(data);
                            resolve(data)
                        });
                    }
                    else {
                        data.then(res=> {
                            if (response.status == 400) {
                                console.log(res.errors[0].defaultMessage);
                                reject(res.errors[0].defaultMessage);
                            }else {
                                reject(res.message)
                            }
                        })
                    }
                });
        });
    }

    getTweetsFromUser(userId:number, page:number, size:number):Promise<Tweet[]> {
        return new Promise<Tweet[]>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + TWEETS_FROM_USER(userId, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((tweets:Tweet[])=> {
                    tweets.forEach(tweet => {
                        this.postHelper.getCurrentUserPostData(tweet);
                    });
                    resolve(tweets);
                })
        });
    }

    getTweetsByTags(tags:Tag[], page:number, size:number) {
        return new Promise<Tweet[]>((resolve, reject)=> {
            if (tags.length > 0) {
                this.httpClient.fetch(BASE_URL + TWEETS_BY_TAGS(tags.map(tag => tag.text), page, size), {
                    method: 'get',
                    headers: {
                        [Const.TOKEN_HEADER]: this.authToken
                    }
                })
                    .then(response => response.json())
                    .then((data:Tweet[]) => {
                        if (data.length > 0) {
                            data.forEach(tweet => this.postHelper.getCurrentUserPostData(tweet));
                        }
                        resolve(data);
                    })

            } else {
                console.error("No tags!");
                resolve([]);
            }
        });
    }

    getMostPopularTweets(hours:number, page:number, size:number):Promise<Tweet[]> {
        return new Promise<Tweet[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + TWEETS_MOST_VOTED(hours, page, size), {
                method: 'get',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then((data:Tweet[]) => {
                    data.forEach(tweet => this.postHelper.getCurrentUserPostData(tweet));
                    resolve(data);
                })
        });
    }

    getMostPopularTweetsWithTags(tags:Tag[], hours:number, page:number, size:number):Promise<Tweet[]> {
        return new Promise<Tweet[]>((resolve, reject)=> {
            if (tags.length > 0) {
                this.httpClient.fetch(BASE_URL + TWEETS_BY_TAGS_POPULAR(tags.map(tag => tag.text), hours, page, size), {
                    method: 'get',
                    headers: {
                        [Const.TOKEN_HEADER]: this.authToken
                    }
                })
                .then(response => response.json())
                .then((data:Tweet[]) => {
                    data.forEach(tweet => this.postHelper.getCurrentUserPostData(tweet));
                    resolve(data);
                })
            } else {
                console.error("No tags!");
                resolve([]);
            }
        });
    }
}
