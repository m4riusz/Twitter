import {customElement, bindable} from "aurelia-templating";
import {inject} from "aurelia-framework";
import {TweetService, ITweetService} from "../../service/tweetService";
import {User} from "../../pages/users/user";
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 17.09.16.
 */

@customElement('tweet-input')
@inject(TweetService)
export class TweetInput {
    @bindable tweets:Tweet[];
    @bindable currentUser:User;

    message:string;
    private tweetService:ITweetService;

    constructor(tweetService:ITweetService) {
        this.tweetService = tweetService;
        this.message = '';
    }

    async send(message:string) {
        try {
            let newTweet = await this.tweetService.send(<Tweet>{
                type: "tweet",
                content: message,
                owner: this.currentUser,
            });
            this.message = '';
            this.tweets.unshift(newTweet);
        } catch (error) {
            alert(error);
        }
    }
}