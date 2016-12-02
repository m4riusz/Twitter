package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.model.Notification;
import com.twitter.service.NotificationService;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mariusz on 02.12.16.
 */

@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class NotificationServiceSecurityTest {

    @Autowired
    private NotificationService notificationService;

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void save_anonymousAccessDenied() {
        notificationService.save(new Notification());
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void notificationService_anonymousAccessDenied() {
        notificationService.getLatestNotifications(true, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void changeNotificationSeen_anonymousAccessDenied() {
        notificationService.changeNotificationSeen(TestUtil.ID_ONE, true);
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void getNotificationById_anonymousAccessDenied() {
        notificationService.getNotificationById(TestUtil.ID_ONE);
    }

}
