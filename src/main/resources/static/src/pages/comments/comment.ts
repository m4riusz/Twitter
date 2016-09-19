import Tweet = Models.Tweet;
import {inject} from "aurelia-dependency-injection";
import {TweetService, ITweetService} from "../../service/tweetService";
import {CommentService, ICommentService} from "../../service/commentService";
import {ITweetContainer, ICommentContainer} from "../../domain/containers";
import {Const} from "../../domain/const";
import {ICommentSender} from "../../domain/senders";
import User = Models.User;
import Vote = Models.Vote;
/**
 * Created by mariusz on 14.09.16.
 */

@inject(TweetService, CommentService)
export class Comment implements ITweetContainer,ICommentContainer,ICommentSender {
    private page;
    tweet:Tweet;
    comments:Models.Comment[];
    tweetContainer:ITweetContainer;
    commentContainer:ICommentContainer;
    currentLoggedUser:User;
    commentSender:ICommentSender;
    private commentService:ICommentService;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService, commentService:ICommentService) {
        this.page = 0;
        this.tweetService = tweetService;
        this.commentService = commentService;
        this.tweetContainer = this;
        this.commentContainer = this;
        this.commentSender = this;
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
        console.log("TODO");
    }

    report(tweet:Models.Tweet) {
        console.log("TODO");
    }


    deleteComment(commentId:number) {
        this.commentService.deleteComment(commentId).then(() => this.updateComment(commentId));
    }

    voteOnComment(commentId:number, vote:Vote) {
        this.commentService.voteComment(commentId, vote).then((vote:Vote) => this.setCommentVote(commentId, vote));
    }

    deleteCommentVote(commentId:number) {
        this.commentService.deleteCommentVote(commentId).then(() => this.setCommentVote(commentId, "NONE"));
    }

    send(message:string) {
        try {
            this.commentService.commentTweet(<Models.Comment>{
                type: "comment",
                content: message,
                owner: this.currentLoggedUser,
                tweet: this.tweet
            })
                .then(comment => {
                    this.comments.unshift(comment);
                });
        } catch (error) {
            console.log(error);
        }
    }

    private setCommentVote(commentId:number, vote:Vote) {
        this.comments.forEach(current => current.id == commentId ? current.loggedUserVote = vote : current);
    }

    private async updateTweet(tweetId:number) {
        this.tweet = await this.tweetService.getTweetById(tweetId);
    }

    private async updateComment(commentId:number) {
        let updated = await this.commentService.getCommentById(commentId);
        this.comments = this.comments.map(current => current.id == commentId ? updated : current);
    }

}