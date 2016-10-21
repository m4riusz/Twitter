import Tag = Models.Tag;
import User = Models.User;
import {inject} from "aurelia-dependency-injection";
import {Const} from "../../domain/const";
import Tweet = Models.Tweet;
import {TweetService, ITweetService} from "../../service/tweetService";
/**
 * Created by mariusz on 19.10.16.
 */

@inject(TweetService)
export class TagView {

    tags:Tag[];
    tweets:Tweet[];
    private page:number;
    private currentLoggedUser:User;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService) {
        this.page = 0;
        this.tweetService = tweetService;
    }

    async activate(params, config) {
        const textTags = params.tagNames.split(Const.SEPARATOR);
        this.tags = textTags.map(tag => {
            return {text: tag}
        });
        this.currentLoggedUser = config.settings.currentUser;
        this.tweets = await this.tweetService.getTweetsByTags(this.tags, this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getTweetsByTags(this.tags, this.page, Const.PAGE_SIZE);
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}