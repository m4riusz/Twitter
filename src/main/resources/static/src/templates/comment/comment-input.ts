import {customElement, bindable} from "aurelia-templating";
import {inject} from "aurelia-framework";
import {CommentService, ICommentService} from "../../service/commentService";
import Tweet = Models.Tweet;
import Comment = Models.Comment;
import User = Models.User;
/**
 * Created by mariusz on 18.09.16.
 */

@customElement('comment-input')
@inject(CommentService)
export class CommentInput {
    @bindable tweet:Tweet;
    @bindable comments:Comment[];
    @bindable currentUser:User;

    message:string;
    private commentService:ICommentService;

    constructor(commentService:ICommentService) {
        this.commentService = commentService;
        this.message = '';
    }

    async send(message:string) {
        try {
            let newComment = await this.commentService.commentTweet(<Models.Comment>{
                type: "comment",
                content: message,
                owner: this.currentUser,
                tweet: this.tweet
            });
            this.message = '';
            this.comments.unshift(newComment);
        } catch (error) {
            alert(error);
        }
    }
}