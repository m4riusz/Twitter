/**
 * Created by mariusz on 30.08.16.
 */

module Models {

    export interface AbstractEntity {
        id:number;
        createDate:string;
    }

    export interface AbstractPost extends AbstractEntity {
        type:string;
        deleted:boolean;
        banned:boolean;
        content:string;
        owner:User;
        votes:UserVote[];
        reports:Report[];
        loggedUserVote:'UP'|'DOWN'|'NONE';
        upVoteCount?:number;
        downVoteCount?:number;
    }

    export interface AccountStatus extends AbstractEntity {
        enable:boolean;
        enableDate:string;
        bannedUntil:string;
        deleted:boolean;
    }

    export interface Avatar extends AbstractEntity {
        fileName:string;
        bytes:number[];
    }

    export interface Comment extends AbstractPost {
        tweet:Tweet;
    }

    export interface Report extends AbstractEntity {
        status:ReportStatus;
        category:ReportCategory;
        message:string;
        user:User;
        judge:User;
        abstractPost:AbstractPost;
    }

    export enum ReportCategory{
        VERBAL_ABUSE,
        HATE_SPEECH,
        PORNOGRAPHY,
        ADVERTISEMENT,
        SPAM_OR_FLOOD,
        WRONG_TAGS,
        OTHER
    }

    export enum ReportStatus{
        WAITING_FOR_REALIZATION,
        INNOCENT,
        GUILTY
    }


    export interface Tag extends AbstractEntity {
        text:string;
    }

    export interface Tweet extends AbstractPost {
        favourite:boolean;
        tags:Tag[];
        comments:Comment[];
    }

    export interface User extends AbstractEntity {
        avatar:Avatar;
        username:string;
        email:string;
        role:'USER' | 'ADMIN' | 'MODERATOR';
        gender:'MALE' | 'FEMALE' | 'UNDEFINED';
        accountStatus:AccountStatus;
        reports:Report[];
        tweets:Tweet[];
        favouriteTags:Tag[];
        followers:User[];
        favouriteTweets:Tweet[];
        authorities:any;
    }

    export interface UserVote extends AbstractEntity {
        vote:'UP' |'DOWN';
        user:User;
        abstractPost:AbstractPost;
    }
}