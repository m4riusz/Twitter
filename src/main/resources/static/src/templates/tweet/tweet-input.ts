import {customElement, bindable} from "aurelia-templating";
import {ITweetSender} from "../../domain/senders";
/**
 * Created by mariusz on 17.09.16.
 */

@customElement('tweet-input')
export class TweetInput {
    @bindable tweetSender:ITweetSender;
    message:string;
}