import Tag = Models.Tag;
import User = Models.User;
import {inject} from "aurelia-dependency-injection";
import {Const} from "../../domain/const";
import {TweetService, ITweetService} from "../../service/tweetService";
import {TagService, ITagService} from "../../service/tagService";
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 19.10.16.
 */

@inject(TweetService, TagService)
export class TagView {
    currentTagFollowed:boolean;
    tags:Tag[];
    tweets:Tweet[];
    private page:number;
    private mode:number;
    private currentLoggedUser:User;
    private tweetService:ITweetService;
    private tagService:ITagService;

    constructor(tweetService:ITweetService, tagService:ITagService) {
        this.page = 0;
        this.mode = 0;
        this.tweetService = tweetService;
        this.tagService = tagService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        if (!config.settings.favourite) {
            const textTags = params.tagNames.split(Const.SEPARATOR);
            this.tags = textTags.map(tag => {
                return {text: tag}
            });
            this.tweets = await this.tweetService.getTweetsByTags(this.tags, this.page, Const.PAGE_SIZE);
            this.currentTagFollowed = this.isTagFavourited(this.tags[0]);
        } else {
            this.tags = this.currentLoggedUser.favouriteTags = await this.tagService.getUserFavouriteTags(this.currentLoggedUser.id);
            this.tweets = await this.tweetService.getTweetsByTags(this.tags, this.page, Const.PAGE_SIZE);
            this.currentTagFollowed = this.isTagFavourited(this.tags[0]);
        }
    }

    setMode(mode:number) {
        this.page = 0;
        this.mode = mode;
        this.getTweetsByMode();
    }

    async addTagToFavourites(tag:Tag) {
        try {
            this.tags[0] = await this.tagService.addTagToFavourites(this.currentLoggedUser.id, tag.text);
            this.currentLoggedUser.favouriteTags.push(this.tags[0]);
            this.currentTagFollowed = true;
        } catch (error) {
            alert(error);
        }
    }

    async deleteTagFromFavourites(tag:Tag) {
        try {
            await this.tagService.removeTagFromFavourites(this.currentLoggedUser.id, tag.text);
            this.currentLoggedUser.favouriteTags = this.currentLoggedUser.favouriteTags.filter(current => current.text !== tag.text);
            this.currentTagFollowed = false;
        } catch (error) {
            alert(error);
        }
    }

    isTagFavourited(tag:Tag):boolean {
        return this.currentLoggedUser.favouriteTags.filter(current => current.text == tag.text).length === 1;
    }

    private async getTweetsByMode() {
        switch (this.mode) {
            case 0:
                this.tweets = await this.tweetService.getTweetsByTags(this.tags, this.page, Const.PAGE_SIZE);
                break;
            case 1:
                this.tweets = await this.tweetService.getMostPopularTweetsWithTags(this.tags, 6, this.page, Const.PAGE_SIZE);
                break;
            case 2:
                this.tweets = await this.tweetService.getMostPopularTweetsWithTags(this.tags, 12, this.page, Const.PAGE_SIZE);
                break;
            case 3:
                this.tweets = await this.tweetService.getMostPopularTweetsWithTags(this.tags, 24, this.page, Const.PAGE_SIZE);
                break;
        }
    }

    async getNextPage() {
        try {
            this.page = ++this.page;
            let nextTweetPage = [];
            switch (this.mode) {
                case 0:
                    nextTweetPage = await this.tweetService.getTweetsByTags(this.tags, this.page, Const.PAGE_SIZE);
                    break;
                case 1:
                    nextTweetPage = await this.tweetService.getMostPopularTweetsWithTags(this.tags, 6, this.page, Const.PAGE_SIZE);
                    break;
                case 2:
                    nextTweetPage = await this.tweetService.getMostPopularTweetsWithTags(this.tags, 12, this.page, Const.PAGE_SIZE);
                    break;
                case 3:
                    nextTweetPage = await this.tweetService.getMostPopularTweetsWithTags(this.tags, 24, this.page, Const.PAGE_SIZE);
                    break;
            }
            this.tweets = this.tweets.concat(nextTweetPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}