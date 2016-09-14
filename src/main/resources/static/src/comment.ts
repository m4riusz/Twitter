import Tweet = Models.Tweet;
import {ICommentService, CommentService} from "./commentService";
import {inject} from "aurelia-dependency-injection";
import {Const} from "./const";
import {ITweetService, TweetService} from "./tweetService";
/**
 * Created by mariusz on 14.09.16.
 */

@inject(TweetService, CommentService)
export class Comment {
    private page;
    tweet:Tweet;
    comments:Models.Comment[];
    private commentService:ICommentService;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService, commentService:ICommentService) {
        this.page = 0;
        this.tweetService = tweetService;
        this.commentService = commentService;
    }

    async activate(params, config) {
        this.tweet = await this.tweetService.getTweetById(params.tweetId);
        this.comments = await this.commentService.getTweetComments(params.tweetId, this.page, Const.PAGE_SIZE);
    }
}