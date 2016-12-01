package com.twitter.service;

import com.twitter.dao.NotificationDao;
import com.twitter.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mariusz on 01.12.16.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private NotificationDao notificationDao;

    @Autowired
    public NotificationServiceImpl(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    @Override
    public Notification save(Notification notification) {
        return notificationDao.save(notification);
    }
}
