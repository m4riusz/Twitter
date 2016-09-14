import {bindable, customElement} from "aurelia-templating";
import {Home} from "./home";
import User = Models.User;
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 03.09.16.
 */

@customElement('tweet-template')
export class TweetTemplate {
    @bindable tweet:Tweet;
    @bindable parent:Home;
    @bindable currentUser:User;
}