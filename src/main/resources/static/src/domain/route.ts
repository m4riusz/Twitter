import Tag = Models.Tag;
/**
 * Created by mariusz on 30.08.16.
 */

export const BASE_URL = `http://localhost:8080`;
export const USER_URL = `/api/user`;
export const REGISTER = USER_URL;
export const CURRENT_USER = USER_URL;
export const LOGIN = `/api/login`;
export const TWEET_URL = `/api/tweet`;
export const COMMENT_URL = `/api/comment`;
export const REPORT_URL = `/api/report`;
export const TAG_URL = `/api/tweet/tags`;
export const USER_BY_ID = (userId:number) => `${USER_URL}/${userId}`;
export const USER_CHANGE_PASSWORD = (userId:number) => `${USER_BY_ID(userId)}/password`;
export const USER_CHANGE_ROLE = (userId:number) => `${USER_BY_ID(userId)}/role`;
export const USER_CHANGE_EMAIL = (userId:number) => `${USER_BY_ID(userId)}/email`;
export const USER_FOLLOWERS_URL = (userId:number) => `${USER_BY_ID(userId)}/followers`;
export const USER_FOLLOWING_URL = (userId:number) => `${USER_BY_ID(userId)}/following`;
export const COMMENT_BY_ID = (commentId:number) => `${COMMENT_URL}/${commentId}`;
export const TWEET_BY_ID = (tweetId:number) => `${TWEET_URL}/${tweetId}`;
export const TWEET_GET_ALL = (page:number, size:number) => `${TWEET_URL}/${page}/${size}`;
export const TWEET_VOTE_GET_BY_ID = (tweetId:number) => `${TWEET_BY_ID(tweetId)}/vote`;
export const TWEET_VOTE = `${TWEET_URL}/vote`;
export const COMMENT_VOTE = `${COMMENT_URL}/vote`;
export const CHANGE_USER_AVATAR = (userId:number) => `${USER_BY_ID(userId)}/avatar`;
export const TWEET_FAVOURITE = (tweetId:number | string) =>`${USER_URL}/favourites/${tweetId}`;
export const USER_FAVOURITE_TWEETS = (userId:number, page:number, size:number) => `${USER_URL}/${userId}/favourites/${page}/${size}`;
export const COMMENTS_FROM_TWEET = (tweetId:number, page:number, size:number) => `${TWEET_BY_ID(tweetId)}/comment/${page}/${size}`;
export const COMMENT_VOTE_BY_ID = (commentId:number) => `${COMMENT_BY_ID(commentId)}/vote`;
export const TWEETS_FROM_USER = (userId:number, page:number, size:number) => `${TWEET_URL}/user/${userId}/${page}/${size}`;
export const TWEETS_FROM_FOLLOWING_USERS = (userId:number, page:number, size:number) => `${TWEET_URL}/my/${userId}/${page}/${size}`;
export const FOLLOW_USER = (userId:number) =>`${USER_URL}/follow/${userId}`;
export const USER_FOLLOWERS = (userId:number, page:number, size:number) => `${USER_FOLLOWERS_URL(userId)}/${page}/${size}`;
export const USER_FOLLOWING = (userId:number, page:number, size:number) => `${USER_FOLLOWING_URL(userId)}/${page}/${size}`;
export const USER_FOLLOWERS_COUNT = (userId:number) => `${USER_FOLLOWERS_URL(userId)}/count`;
export const USER_FOLLOWING_COUNT = (userId:number) => `${USER_FOLLOWING_URL(userId)}/count`;
export const USER_REPORTS = (page:number, size:number) => `${REPORT_URL}/${page}/${size}`;
export const REPORTS_LATEST = (page:number, size:number) => `${REPORT_URL}/latest/${page}/${size}`;
export const REPORTS_CATEGORY = (category:string, page:number, size:number) => `${REPORT_URL}/category/${category}/${page}/${size}`;
export const REPORTS_STATUS = (status:string, page:number, size:number) => `${REPORT_URL}/status/${status}/${page}/${size}`;
export const REPORTS_BY_STATUS_AND_CATEGORY = (status:string, category:string, page:number, size:number) => `${REPORT_URL}/status/${status}/category/${category}/${page}/${size}`;
export const TWEETS_BY_TAGS = (tags:string[], page:number, size:number) => `${TAG_URL}/${tags.join(',')}/${page}/${size}`;
export const TWEETS_MOST_VOTED = (hours:number, page:number, size:number) => `${TWEET_URL}/popular/${hours}/${page}/${size}`;
export const USER_FAVOURITE_TAGS = (userId:number) => `${USER_BY_ID(userId)}/tags`;
export const TWEET_VOTE_COUNT = (tweetId:number, vote:string) => `${TWEET_BY_ID(tweetId)}/count/vote/${vote}`;
export const COMMENT_VOTE_COUNT = (commentId:number, vote:string) => `${COMMENT_BY_ID(commentId)}/count/vote/${vote}`;