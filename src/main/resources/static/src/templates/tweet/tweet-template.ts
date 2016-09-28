import {bindable, customElement} from "aurelia-templating";
import {inject} from "aurelia-framework";
import {ITweetService, TweetService} from "../../service/tweetService";
import {IReportService, ReportService} from "../../service/reportService";
import {DialogService} from "aurelia-dialog";
import {Router} from "aurelia-router";
import {ReportModal} from "../report/report-modal";
import User = Models.User;
import Tweet = Models.Tweet;
import Report = Models.Report;
import Vote = Models.Vote;
/**
 * Created by mariusz on 03.09.16.
 */

@customElement('tweet-template')
@inject(TweetService, ReportService, DialogService)
export class TweetTemplate {
    @bindable tweet:Tweet;
    @bindable currentUser:User;
    private tweetService:ITweetService;
    private reportService:IReportService;
    private dialogService:DialogService;

    constructor(tweetService:ITweetService, reportService:IReportService, dialogService:DialogService) {
        this.tweetService = tweetService;
        this.reportService = reportService;
        this.dialogService = dialogService;
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId).then(()=> this.updateTweet(tweetId));
    }

    voteOnTweet(tweetId:number, vote:Vote) {
        this.tweetService.voteTweet(tweetId, vote).then((vote)=> this.setTweetVote(vote));
    }

    deleteTweetVote(tweetId:number) {
        this.tweetService.deleteVote(tweetId).then(()=> this.setTweetVote('NONE'));
    }

    addTweetToFavourites(tweetId:number) {
        this.tweetService.addTweetToFavourites(tweetId).then(tweet => this.setFavourite(true));
    }

    deleteTweetFromFavourites(tweetId:number) {
        this.tweetService.removeTweetFromFavourites(tweetId).then(() => this.setFavourite(false));
    }

    report(tweet:Tweet) {
        this.dialogService.open({viewModel: ReportModal}).then(response => {
            if (!response.wasCancelled) {
                let reportCategory = response.output.cat.id;
                let reportMessage = response.output.msg;
                this.reportService.send(<Models.Report>{
                    category: reportCategory,
                    message: reportMessage,
                    user: this.currentUser,
                    abstractPost: tweet
                }).then((report:Report)=> {
                    alert('Thank you for the report!');
                })
            }
        })
    }

    private async updateTweet(tweetId:number) {
        this.tweet = await this.tweetService.getTweetById(tweetId);
    }

    private setTweetVote(vote) {
        this.tweet.loggedUserVote = vote;
    }

    private setFavourite(favourite:boolean) {
        this.tweet.favourite = favourite;
    }

}