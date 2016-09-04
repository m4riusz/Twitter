/**
 * Created by mariusz on 30.08.16.
 */

export const BASE_URL = `http://localhost:8080`;
export const REGISTER = `/api/user`;
export const CURRENT_USER = `/api/user`;
export const LOGIN = `/api/login`;
export const TWEET_URL = `/api/tweet`;
export const TWEET_BY_ID = (tweetId:number) => `${TWEET_URL}/${tweetId}`;
export const TWEET_GET_ALL = (page:number, size:number) => `${TWEET_URL}/${page}/${size}`;


