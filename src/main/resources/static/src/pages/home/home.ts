import {inject} from "aurelia-framework";
import {Router, RouteConfig} from "aurelia-router";
import {TweetService, ITweetService} from "../../service/tweetService";
import {ITweetContainer} from "../../domain/containers";
import {Const} from "../../domain/const";
import {ITweetSender} from "../../domain/senders";
import {DialogService} from "aurelia-dialog";
import {ReportModal} from "../../templates/report/report-modal";
import {IReportService, ReportService} from "../../service/reportService";
import Tweet = Models.Tweet;
import Vote = Models.Vote;
import User = Models.User;
import Report = Models.Report;

/**
 * Created by mariusz on 31.08.16.
 */

@inject(TweetService, ReportService, Router, DialogService)
export class Home implements ITweetContainer, ITweetSender {
    currentLoggedUser:User;
    page:number;
    tweets:Tweet[];
    tweetContainer:ITweetContainer;
    tweetSender:ITweetSender;
    private router:Router;
    private tweetService:ITweetService;
    private reportService:IReportService;
    private dialogService:DialogService;

    constructor(tweetService:ITweetService, reportService:IReportService, router:Router, dialogService:DialogService) {
        this.page = 0;
        this.router = router;
        this.tweetService = tweetService;
        this.reportService = reportService;
        this.dialogService = dialogService;
        this.tweetContainer = this;
        this.tweetSender = this;
    }

    async activate(params, routeConfig:RouteConfig) {
        this.currentLoggedUser = routeConfig.settings.currentUser;
        this.tweets = await this.tweetService.getAllTweets(this.page, Const.PAGE_SIZE);
    }

    deleteTweet(tweetId:number) {
        this.tweetService.deleteTweet(tweetId).then(()=> this.updateTweet(tweetId));
    }

    voteOnTweet(tweetId:number, vote:Vote) {
        this.tweetService.voteTweet(tweetId, vote).then((vote)=> this.setTweetVote(tweetId, vote));
    }

    deleteTweetVote(tweetId:number) {
        this.tweetService.deleteVote(tweetId).then(()=> this.setTweetVote(tweetId, 'NONE'));
    }

    addTweetToFavourites(tweetId:number) {
        this.tweetService.addTweetToFavourites(tweetId).then(tweet =>this.setTweetFavourite(tweetId, true));
    }

    deleteTweetFromFavourites(tweetId:number) {
        this.tweetService.removeTweetFromFavourites(tweetId).then(()=>this.setTweetFavourite(tweetId, false));
    }

    showComments(tweet:Tweet) {
        this.router.navigate(`comment/${tweet.id}`, {tweetId: tweet.id});
    }

    report(tweet:Tweet) {
        this.dialogService.open({viewModel: ReportModal}).then(response => {
            if (!response.wasCancelled) {
                let reportCategory = response.output.cat.id;
                let reportMessage = response.output.msg;
                this.reportService.send(<Models.Report>{
                    category: reportCategory,
                    message: reportMessage,
                    user: this.currentLoggedUser,
                    abstractPost: tweet
                }).then((report:Report)=> {
                    alert('Thank you for the report!');
                })
            }
        })
    }

    async send(message:string) {
        try {
            let newTweet = await this.tweetService.send(<Tweet>{
                type: "tweet",
                content: message != null ? message : '',
                owner: this.currentLoggedUser,
            });
            this.tweets.unshift(newTweet);
        } catch (error) {
            alert(error);
        }
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getAllTweets(this.page, Const.PAGE_SIZE);
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

    private setTweetFavourite(tweetId:number, favourite:boolean) {
        this.tweets.forEach(current => current.id == tweetId ? current.favourite = favourite : current);
    }

    private setTweetVote(tweetId:number, vote:Vote) {
        this.tweets.forEach(current => current.id == tweetId ? current.loggedUserVote = vote : current);
    }

    private async updateTweet(tweetId:number) {
        let updated = await this.tweetService.getTweetById(tweetId);
        this.tweets = this.tweets.map(tweet => tweet.id == tweetId ? tweet = updated : tweet);
    }


}
