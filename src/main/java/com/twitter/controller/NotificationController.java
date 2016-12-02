package com.twitter.controller;

import com.twitter.model.Notification;
import com.twitter.route.Route;
import com.twitter.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mariusz on 02.12.16.
 */
@RestController
public class NotificationController {

    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RequestMapping(value = Route.NOTIFICATION_GET, method = RequestMethod.GET)
    public List<Notification> getLatestNotificationsFromLoggedUser(@PathVariable boolean seen, @PathVariable int page, @PathVariable int size) {
        return notificationService.getLatestNotifications(seen, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.NOTIFICATION_BY_ID, method = RequestMethod.GET)
    public Notification getNotificationById(@PathVariable int notificationId) {
        return notificationService.getNotificationById(notificationId);
    }

    @RequestMapping(value = Route.NOTIFICATION_BY_ID, method = RequestMethod.PUT)
    public Notification changeNotificationStatus(@PathVariable int notificationId, @RequestBody boolean seen) {
        return notificationService.changeNotificationSeen(notificationId, seen);
    }

}
