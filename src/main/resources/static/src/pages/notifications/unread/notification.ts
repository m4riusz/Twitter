import {INotificationService, NotificationService} from "../../../service/notificationService";
import {inject} from "aurelia-dependency-injection";
import {Const} from "../../../domain/const";
import {EventAggregator} from "aurelia-event-aggregator";
import Notification = Models.Notification;
import User = Models.User;
/**
 * Created by mariusz on 03.12.16.
 */

@inject(NotificationService, EventAggregator)
export class UnreadNotifications{

    page:number;
    notifications:Notification[];
    currentLoggedUser:User;
    private notificationService:INotificationService;
    private eventAggregator:EventAggregator;
    private aggregator:any;

    constructor(notificationService:INotificationService, eventAggregator:EventAggregator) {
        this.page = 0;
        this.notificationService = notificationService;
        this.eventAggregator = eventAggregator;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.notifications = await this.notificationService.getLatestNotifications(false, this.page, Const.PAGE_SIZE);
        this.eventAggregator.publish("notifications", this.notifications);
    }

    async nextCommentPage() {
        try {
            this.page = ++this.page;
            let nexNotificationPage = await this.notificationService.getLatestNotifications(false, this.page, Const.PAGE_SIZE);
            this.notifications = this.notifications.concat(nexNotificationPage);
        } catch (error) {
            this.page = --this.page;
        }
    }

}