/**
 * Created by mariusz on 17.09.16.
 */


export interface ITweetSender{
    send(content:string);
}

export interface ICommentSender {
    send(content:string);
}