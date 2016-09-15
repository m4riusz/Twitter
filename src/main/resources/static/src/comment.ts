import Tweet = Models.Tweet;
import {ICommentService, CommentService} from "./commentService";
import {inject} from "aurelia-dependency-injection";
import {Const} from "./const";
import {ITweetService, TweetService} from "./tweetService";
import {ITweetContainer} from "./tweetContainer";
import User = Models.User;
import Vote = Models.Vote;
/**
 * Created by mariusz on 14.09.16.
 */

@inject(TweetService, CommentService)
export class Comment implements ITweetContainer {
    private page;
    tweet:Tweet;
    comments:Models.Comment[];
    tweetContainer:ITweetContainer;
    currentLoggedUser:User;
    private commentService:ICommentService;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService, commentService:ICommentService) {
        this.page = 0;
        this.tweetService = tweetService;
        this.commentService = commentService;
        this.tweetContainer = this;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        [this.tweet, this.comments] = await Promise.all(
            [
                this.tweetService.getTweetById(params.tweetId),
                this.commentService.getTweetComments(params.tweetId, this.page, Const.PAGE_SIZE)
            ]
        );
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId).then(()=> this.updateTweet(tweetId));
    }

    voteOnTweet(tweetId:number, vote:Vote) {
        this.tweetService.voteTweet(tweetId, vote).then((vote)=> this.tweet.loggedUserVote = vote);
    }

    deleteTweetVote(tweetId:number) {
        this.tweetService.deleteVote(tweetId).then(()=> this.tweet.loggedUserVote = "NONE");
    }

    addTweetToFavourites(tweetId:number) {
        this.tweetService.addTweetToFavourites(tweetId).then(tweet =>this.tweet.favourite = true);
    }

    deleteTweetFromFavourites(tweetId:number) {
        this.tweetService.removeTweetFromFavourites(tweetId).then(()=>this.tweet.favourite = false);
    }

    showComments(tweet:Tweet) {
    }

    private async updateTweet(tweetId:number) {
        this.tweet = await this.tweetService.getTweetById(tweetId);
    }

}