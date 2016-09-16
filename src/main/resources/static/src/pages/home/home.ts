import {inject} from "aurelia-dependency-injection";
import {Router, RouteConfig} from "aurelia-router";
import {TweetService, ITweetService} from "../../service/tweetService";
import {ITweetContainer} from "../../domain/containers";
import {Const} from "../../domain/const";
import Tweet = Models.Tweet;
import Vote = Models.Vote;
import User = Models.User;

/**
 * Created by mariusz on 31.08.16.
 */

@inject(TweetService, Router)
export class Home implements ITweetContainer {
    currentLoggedUser:User;
    page:number;
    tweets:Tweet[];
    tweetContainer:ITweetContainer;
    private router:Router;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService, router:Router) {
        this.page = 0;
        this.router = router;
        this.tweetService = tweetService;
        this.tweetContainer = this;
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
