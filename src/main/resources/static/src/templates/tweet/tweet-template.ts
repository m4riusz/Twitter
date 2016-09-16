import {bindable, customElement} from "aurelia-templating";
import {ITweetContainer} from "../../domain/containers";
import User = Models.User;
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 03.09.16.
 */

@customElement('tweet-template')
export class TweetTemplate {
    @bindable tweet:Tweet;
    @bindable tweetContainer:ITweetContainer;
    @bindable currentUser:User;
}