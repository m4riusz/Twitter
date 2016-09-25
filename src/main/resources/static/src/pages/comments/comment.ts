import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {CommentService, ICommentService} from "../../service/commentService";
import {Const} from "../../domain/const";
import User = Models.User;
import Vote = Models.Vote;
import Tweet = Models.Tweet;
import AbstractPost = Models.AbstractPost;
import Report = Models.Report;
/**
 * Created by mariusz on 14.09.16.
 */

@inject(TweetService, CommentService)
export class Comment {
    private page;
    tweet:Tweet;
    comments:Models.Comment[];
    currentLoggedUser:User;
    private commentService:ICommentService;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService, commentService:ICommentService) {
        this.page = 0;
        this.tweetService = tweetService;
        this.commentService = commentService;
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

    async nextCommentPage() {
        try {
            this.page = ++this.page;
            let nextCommentPage = await this.commentService.getTweetComments(this.tweet.id, this.page, Const.PAGE_SIZE);
            this.comments = this.comments.concat(nextCommentPage);
        } catch (error) {
            this.page = --this.page;
        }
    }
}