import User = Models.User;
import Tweet = Models.Tweet;
import {ITweetService, TweetService} from "../../service/tweetService";
import {inject} from "aurelia-dependency-injection";
import {RouteConfig} from "aurelia-router";
import {Const} from "../../domain/const";
import {TagService, ITagService} from "../../service/tagService";
import Tag = Models.Tag;
/**
 * Created by mariusz on 26.10.16.
 */
@inject(TweetService, TagService)
export class TweetsByTags {
    currentLoggedUser:User;
    page:number;
    tweets:Tweet[];
    currentTagFollowed:boolean;
    private tweetService:ITweetService;
    private tagService:ITagService;

    constructor(tweetService:ITweetService, tagService:ITagService) {
        this.page = 0;
        this.tweetService = tweetService;
        this.tagService = tagService;
    }

    async activate(params, routeConfig:RouteConfig) {
        this.currentLoggedUser = routeConfig.settings.currentUser;
        this.currentLoggedUser.favouriteTags = await this.tagService.getUserFavouriteTags(this.currentLoggedUser.id);
        this.currentTagFollowed = this.isTagFavourited(this.currentLoggedUser.favouriteTags[0]);
        this.tweets = await this.tweetService.getTweetsByTags(this.currentLoggedUser.favouriteTags, this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = await this.tweetService.getTweetsByTags(this.currentLoggedUser.favouriteTags, this.page, Const.PAGE_SIZE);
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

    isTagFavourited(tag:Tag):boolean {
        return this.currentLoggedUser.favouriteTags.filter(current => current.text == tag.text).length === 1;
    }


}