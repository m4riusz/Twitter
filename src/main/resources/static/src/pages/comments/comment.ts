import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {CommentService, ICommentService} from "../../service/commentService";
import {ITweetContainer, ICommentContainer} from "../../domain/containers";
import {Const} from "../../domain/const";
import {ICommentSender} from "../../domain/senders";
import {ReportService, IReportService} from "../../service/reportService";
import {DialogService} from "aurelia-dialog";
import {ReportModal} from "../../templates/report/report-modal";
import User = Models.User;
import Vote = Models.Vote;
import Tweet = Models.Tweet;
import AbstractPost = Models.AbstractPost;
import Report = Models.Report;
/**
 * Created by mariusz on 14.09.16.
 */

@inject(TweetService, CommentService, ReportService, DialogService)
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
    private reportService:IReportService;
    private dialogService:DialogService;

    constructor(tweetService:ITweetService, commentService:ICommentService, reportService:IReportService, dialogService:DialogService) {
        this.page = 0;
        this.tweetService = tweetService;
        this.commentService = commentService;
        this.reportService = reportService;
        this.dialogService = dialogService;
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

    report(tweet:Tweet) {
        this.reportPost(tweet);
    }

    reportComment(comment:Comment) {
        this.reportPost(comment);
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

    async send(message:string) {
        try {
            let newComment = await this.commentService.commentTweet(<Models.Comment>{
                type: "comment",
                content: message,
                owner: this.currentLoggedUser,
                tweet: this.tweet
            });
            this.comments.unshift(newComment);
        } catch (error) {
            alert(error);
        }
    }

    async nextCommentPage() {
        try {
            this.page = ++this.page;
            let nextCommentPage = await this.commentService.getTweetComments(this.tweet.id, this.page, Const.PAGE_SIZE);
            this.comments = this.comments.concat(nextCommentPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

    private reportPost(post:AbstractPost) {
        this.dialogService.open({viewModel: ReportModal}).then(response => {
            if (!response.wasCancelled) {
                let reportCategory = response.output.cat.id;
                let reportMessage = response.output.msg;
                this.reportService.send(<Models.Report>{
                    category: reportCategory,
                    message: reportMessage,
                    user: this.currentLoggedUser,
                    abstractPost: post
                }).then((report:Report)=> {
                    alert('Thank you for the report!');
                })
            }
        })
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