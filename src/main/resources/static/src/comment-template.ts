import {customElement, bindable} from "aurelia-templating";
import {ICommentContainer} from "./containers";
import Comment = Models.Comment;
import Tweet = Models.Tweet;
import User = Models.User;
/**
 * Created by mariusz on 15.09.16.
 */

@customElement('comment-template')
export class CommentTemplate {
    @bindable comment:Comment;
    @bindable currentUser:User;
    @bindable commentContainer:ICommentContainer;
}