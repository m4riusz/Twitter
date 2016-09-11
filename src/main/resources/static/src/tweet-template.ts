import {bindable, customElement} from "aurelia-templating";
import {Home} from "./home";
import User = Twitter.Models.User;
/**
 * Created by mariusz on 03.09.16.
 */

@customElement('tweet-template')
export class TweetTemplate {
    @bindable tweet:Twitter.Models.Tweet;
    @bindable parent:Home;
    @bindable currentUser:User;
}