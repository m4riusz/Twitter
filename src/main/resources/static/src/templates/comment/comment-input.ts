import {customElement, bindable} from "aurelia-templating";
import {ICommentSender} from "../../domain/senders";
/**
 * Created by mariusz on 18.09.16.
 */

@customElement('comment-input')
export class CommentInput {
    @bindable commentSender:ICommentSender;
    message:string;
}