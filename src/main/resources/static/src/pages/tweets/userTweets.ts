import Tweet = Models.Tweet;
import User = Models.User;
import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {Const} from "../../domain/const";
/**
 * Created by mariusz on 28.09.16.
 */

@inject(TweetService)
export class UserTweets {
    currentLoggedUser:User;
    userId:number;
    userTweets:Tweet[];
    private page:number;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService) {
        this.page = 0;
        this.tweetService = tweetService;
    }

    async activate(params, config) {
        this.userId = params.userId;
        this.currentLoggedUser = config.settings.currentUser;
        this.userTweets = await this.tweetService.getTweetsFromUser(this.userId, this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getTweetsFromUser(this.userId, this.page, Const.PAGE_SIZE);
            this.userTweets = this.userTweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}