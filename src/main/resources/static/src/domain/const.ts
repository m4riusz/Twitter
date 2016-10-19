/**
 * Created by mariusz on 23.08.16.
 */
export class Const {
    public static LOGIN_LENGTH = {
        MIN: 3,
        MAX: 10
    };
    public static PASSWORD_LENGTH = {
        MIN: 6,
        MAX: 10
    };
    public static TOKEN_HEADER:string = 'Auth-Token';
    public static UNAUTHORIZE_ROOT:string = 'unauthorize';
    public static APP_ROOT:string = 'app';
    public static PAGE_SIZE:number = 10;
    public static REPORT_MESSAGE_MAX_LENGTH:number = 100;
    public static SEPARATOR = ',';
}
