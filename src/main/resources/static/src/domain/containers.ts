
import Vote = Models.Vote;
import Tweet = Models.Tweet;
/**
 * Created by mariusz on 15.09.16.
 */


export interface ITweetContainer {
    deleteTweet(tweetId:number);
    voteOnTweet(tweetId:number, vote:Vote);
    deleteTweetVote(tweetId:number);
    addTweetToFavourites(tweetId:number);
    deleteTweetFromFavourites(tweetId:number);
    showComments(tweet:Tweet);
    report(tweet:Tweet);
}

export interface ICommentContainer {
    deleteComment(commentId:number);
    voteOnComment(commentId:number, vote:Vote);
    deleteCommentVote(commentId:number);
    reportComment(comment:Comment);
}