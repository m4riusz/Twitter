import Notification = Models.Notification;
import User = Models.User;
import {bindable, customElement} from "aurelia-templating";
import {inject} from "aurelia-dependency-injection";
import {NotificationService, INotificationService} from "../../service/notificationService";
/**
 * Created by mariusz on 03.12.16.
 */

@inject(NotificationService)
@customElement('notification-template')
export class NotificationTemplate {

    @bindable notification:Notification;
    @bindable currentUser:User;
    private notificationService:INotificationService;


    constructor(notificationService:INotificationService) {
        this.notificationService = notificationService;
    }

    async mark(seen:boolean) {
        this.notification = await this.notificationService.changeNotificationStatus(this.notification.id, seen);
    }
}