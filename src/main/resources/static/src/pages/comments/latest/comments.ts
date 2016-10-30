import Tweet = Models.Tweet;
import User = Models.User;
import {ICommentService, CommentService} from "../../../service/commentService";
import {inject} from "aurelia-framework";
import {Const} from "../../../domain/const";
import {TweetService, ITweetService} from "../../../service/tweetService";
/**
 * Created by mariusz on 30.10.16.
 */

@inject(CommentService, TweetService)
export class Comments {
    tweet:Tweet;
    comments:Models.Comment[];
    currentLoggedUser:User;
    private page;
    private commentService:ICommentService;
    private tweetService:ITweetService;

    constructor(commentService:ICommentService, tweetService:ITweetService) {
        this.page = 0;
        this.commentService = commentService;
        this.tweetService = tweetService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        [this.tweet, this.comments] = await Promise.all([
            this.tweetService.getTweetById(params.tweetId),
            this.commentService.getLatestCommentsFromTweet(params.tweetId, this.page, Const.PAGE_SIZE)
        ]);
    }

    async nextCommentPage() {
        try {
            this.page = ++this.page;
            let nextCommentPage = await this.commentService.getLatestCommentsFromTweet(this.tweet.id, this.page, Const.PAGE_SIZE);
            this.comments = this.comments.concat(nextCommentPage);
        } catch (error) {
            this.page = --this.page;
        }
    }
}