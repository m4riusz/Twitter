package com.twitter.service;

import com.twitter.exception.NotificationNotFound;
import com.twitter.model.Notification;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 01.12.16.
 */

@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface NotificationService {

    // TODO: 01.12.16 add tests
    Notification save(Notification notification);

    // TODO: 02.12.16 add tests
    List<Notification> getLatestNotifications(boolean seen, Pageable pageable);

    // TODO: 02.12.16 add tests
    Notification changeNotificationSeen(long notificationId, boolean seen);

    // TODO: 02.12.16 add tests
    Notification getNotificationById(long notificationId) throws NotificationNotFound;
}
