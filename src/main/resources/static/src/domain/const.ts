/**
 * Created by mariusz on 23.08.16.
 */
export class Const {
    public static LOGIN_LENGTH = {
        MIN: 6,
        MAX: 20
    };
    public static PASSWORD_LENGTH = {
        MIN: 6,
        MAX: 16
    };
    public static POST_LENGTH = {
        min: 1,
        max: 300
    };
    public static REPORT_LENGTH = {
        min: 0,
        max: 100
    };
    public static TOKEN_HEADER:string = 'Auth-Token';
    public static UNAUTHORIZE_ROOT:string = 'pages/unauthorized/unauthorize';
    public static APP_ROOT:string = 'pages/app';
    public static PAGE_SIZE:number = 10;
    public static SEPARATOR = ',';
    public static NOTIFICATION_EVENT:string = 'notifications';
}
