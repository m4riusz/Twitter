import {INotificationService, NotificationService} from "../../../service/notificationService";
import {inject} from "aurelia-dependency-injection";
import {Const} from "../../../domain/const";
import Notification = Models.Notification;
import User = Models.User;
/**
 * Created by mariusz on 03.12.16.
 */

@inject(NotificationService)
export class AlreadyReadNotifications{

    page:number;
    notifications:Notification[];
    currentLoggedUser:User;
    private notificationService:INotificationService;

    constructor(notificationService:INotificationService) {
        this.page = 0;
        this.notificationService = notificationService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.notifications = await this.notificationService.getLatestNotifications(true, this.page, Const.PAGE_SIZE);
    }

    async nextCommentPage() {
        try {
            this.page = ++this.page;
            let nexNotificationPage = await this.notificationService.getLatestNotifications(true, this.page, Const.PAGE_SIZE);
            this.notifications = this.notifications.concat(nexNotificationPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}