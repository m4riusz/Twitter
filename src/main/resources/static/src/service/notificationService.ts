import Notification = Models.Notification;
import {BasicService} from "./basicService";
import {inject} from "aurelia-dependency-injection";
import {HttpClient} from "aurelia-fetch-client";
import {Const} from "../domain/const";
import {BASE_URL, NOTIFICATION_LATEST, NOTIFICATION_BY_ID} from "../domain/route";
/**
 * Created by mariusz on 03.12.16.
 */


export interface INotificationService {
    getLatestNotifications(seen:boolean, page:number, size:number):Promise<Notification[]>;
    getNotificationById(notificationId:number):Promise<Notification>
    changeNotificationStatus(notificationId:number, seen:boolean):Promise<Notification>;
}

@inject(HttpClient)
export class NotificationService extends BasicService implements INotificationService {
    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    getLatestNotifications(seen:boolean, page:number, size:number):Promise<Notification[]> {
        return new Promise<Notification[]>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + NOTIFICATION_LATEST(seen, page, size), {
                method: "GET",
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(
                    notification => resolve(notification),
                    error => reject(error.message)
                );
        });
    }

    getNotificationById(notificationId:number):Promise<Notification> {
        return new Promise<Notification>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + NOTIFICATION_BY_ID(notificationId), {
                method: "GET",
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(
                    notification => resolve(notification),
                    error => reject(error.message)
                );
        });
    }

    changeNotificationStatus(notificationId:number, seen:boolean):Promise<Notification> {
        return new Promise<Notification>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + NOTIFICATION_BY_ID(notificationId), {
                method: "PUT",
                body: seen,
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(
                    notification => resolve(notification),
                    error => reject(error.message)
                );
        });
    }

}