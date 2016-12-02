package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.NotificationDao;
import com.twitter.exception.NotificationNotFound;
import com.twitter.model.Notification;
import com.twitter.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.twitter.builders.NotificationBuilder.notification;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.TestUtil.ALL_IN_ONE_PAGE;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 02.12.16.
 */
@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

    @Mock
    private NotificationDao notificationDao;
    @Mock
    private UserService userService;

    private NotificationService notificationService;

    @Before
    public void setUp() {
        notificationService = new NotificationServiceImpl(notificationDao, userService);
    }

    @Test
    public void save_test() {
        Notification notification = a(notification());
        when(notificationDao.save(notification)).thenReturn(notification);
        Notification savedNotification = notificationService.save(notification);
        assertThat(savedNotification, is(notification));
    }

    @Test
    public void getLatestNotifications_noNotifications() {
        User user = a(user());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(user, true, ALL_IN_ONE_PAGE)).thenReturn(emptyList());
        List<Notification> latestNotifications = notificationService.getLatestNotifications(true, ALL_IN_ONE_PAGE);
        assertThat(latestNotifications, is(emptyList()));
    }

    @Test
    public void getLatestNotifications_pagingTest() {
        User user = a(user());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        Notification notificationOne = a(notification().withId(1));
        Notification notificationTwo = a(notification().withId(2));
        Notification notificationThree = a(notification().withId(3));
        Notification notificationFour = a(notification().withId(4));
        PageRequest pageOne = new PageRequest(0, 2);
        PageRequest pageTwo = new PageRequest(1, 2);
        when(notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(user, true, pageOne)).thenReturn(aListWith(notificationOne, notificationTwo));
        when(notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(user, true, pageTwo)).thenReturn(aListWith(notificationThree, notificationFour));
        List<Notification> latestNotificationPageOne = notificationService.getLatestNotifications(true, pageOne);
        List<Notification> latestNotificationPageTwo = notificationService.getLatestNotifications(true, pageTwo);
        assertThat(latestNotificationPageOne, contains(notificationOne, notificationTwo));
        assertThat(latestNotificationPageTwo, contains(notificationThree, notificationFour));
    }

    @Test
    public void changeNotificationSeen_exists() {
        User userOne = a(user());
        User userTwo = a(user());
        Notification notification = a(notification()
                .withSourceUser(userOne)
                .withDestinationUser(userTwo)
                .withSeen(false)
        );
        when(userService.getCurrentLoggedUser()).thenReturn(userTwo);
        when(notificationDao.findOne(anyLong())).thenReturn(notification);
        Notification changedNotification = notificationService.changeNotificationSeen(notification.getId(), true);
        assertThat(changedNotification.isSeen(), is(true));
    }

    @Test(expected = NotificationNotFound.class)
    public void changeNotificationSeen_notificationDoesNotExist() {
        User user = a(user());
        Notification notification = a(notification()
                .withSeen(false)
        );
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(notificationDao.findOne(anyLong())).thenReturn(null);
        notificationService.changeNotificationSeen(notification.getId(), true);
    }

    @Test(expected = NotificationNotFound.class)
    public void getNotificationById_notificationDoesNotExist() {
        User user = a(user());
        Notification notification = a(notification()
                .withSeen(false)
        );
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(notificationDao.findOne(anyLong())).thenReturn(null);
        notificationService.getNotificationById(notification.getId());
    }

    @Test
    public void getNotificationById_notificationExists() {
        User user = a(user());
        Notification notification = a(notification()
                .withDestinationUser(user)
        );
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(notificationDao.findOne(anyLong())).thenReturn(notification);
        Notification foundNotification = notificationService.getNotificationById(notification.getId());
        assertThat(foundNotification, is(notification));
    }


}
