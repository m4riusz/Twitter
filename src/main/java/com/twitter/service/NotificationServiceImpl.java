package com.twitter.service;

import com.twitter.dao.NotificationDao;
import com.twitter.exception.NotificationNotFound;
import com.twitter.model.Notification;
import com.twitter.model.User;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mariusz on 01.12.16.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private NotificationDao notificationDao;
    private UserService userService;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao, UserService userService) {
        this.notificationDao = notificationDao;
        this.userService = userService;
    }

    @Override
    public Notification save(Notification notification) {
        return notificationDao.save(notification);
    }

    @Override
    public List<Notification> getLatestNotifications(boolean seen, Pageable pageable) {
        User currentLoggedUser = userService.getCurrentLoggedUser();
        return notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(currentLoggedUser, seen, pageable);
    }

    @Override
    public Notification changeNotificationSeen(long notificationId, boolean seen) {
        Notification notification = getNotificationById(notificationId);
        notification.setSeen(seen);
        return notification;
    }

    @Override
    public Notification getNotificationById(long notificationId) throws NotificationNotFound {
        User currentLoggedUser = userService.getCurrentLoggedUser();
        Notification notification = notificationDao.findOne(notificationId);
        if (notification == null) {
            throw new NotificationNotFound(MessageUtil.NOTIFICATION_DOES_NOT_EXISTS);
        } else if (!userIsOwnerOfNotification(currentLoggedUser, notification)) {
            throw new AccessDeniedException(MessageUtil.ACCESS_DENIED);
        }
        return notification;
    }

    private boolean userIsOwnerOfNotification(User currentLoggedUser, Notification notification) {
        return notification.getDestinationUser().equals(currentLoggedUser);
    }
}
