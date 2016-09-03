import {bindable, customElement} from "aurelia-templating";
/**
 * Created by mariusz on 03.09.16.
 */

@customElement('tweet-template')
export class TweetTemplate {
    @bindable tweet:Twitter.Models.Tweet;
    
}