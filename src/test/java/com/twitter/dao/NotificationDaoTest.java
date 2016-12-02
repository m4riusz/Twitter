package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.Notification;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.builders.NotificationBuilder.notification;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by mariusz on 02.12.16.
 */

@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NotificationDaoTest {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TweetDao tweetDao;

    @Test
    public void findByDestinationUserAndSeenOrderByCreateDateDesc_userHasZeroNotifications() {
        User user = a(user());
        userDao.save(user);
        notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(user, false, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test
    public void findByDestinationUserAndSeenOrderByCreateDateDesc_oneUserSomeNotifications() {
        User sourceUser = a(user());
        User destinationUser = a(user());

        userDao.save(aListWith(sourceUser, destinationUser));
        Tweet tweetOne = a(tweet()
                .withOwner(sourceUser)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(sourceUser)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo));

        Notification notificationOne = a(notification()
                .withAbstractPost(tweetOne)
                .withSourceUser(sourceUser)
                .withDestinationUser(destinationUser)
                .withSeen(false)
        );

        Notification notificationTwo = a(notification()
                .withAbstractPost(tweetTwo)
                .withSourceUser(sourceUser)
                .withDestinationUser(destinationUser)
                .withSeen(false)
        );
        notificationDao.save(aListWith(notificationOne, notificationTwo));

        List<Notification> userDestinationNotifications = notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(destinationUser, false, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userDestinationNotifications, hasSize(2));
        assertThat(userDestinationNotifications, hasItems(notificationOne, notificationTwo));
        assertTrue(userDestinationNotifications.stream().allMatch(notification -> notification.getDestinationUser().equals(destinationUser)));
        assertTrue(userDestinationNotifications.stream().allMatch(notification -> notification.getSourceUser().equals(sourceUser)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void findByDestinationUserAndSeenOrderByCreateDateDesc_userCantNotifyHimself() {
        User user = a(user());
        userDao.save(user);
        Tweet tweetOne = a(tweet()
                .withOwner(user)
        );
        tweetDao.save(tweetOne);

        Notification notificationOne = a(notification()
                .withAbstractPost(tweetOne)
                .withSourceUser(user)
                .withDestinationUser(user)
                .withSeen(false)
        );
        notificationDao.save(notificationOne);
    }

    @Test
    public void findByDestinationUserAndSeenOrderByCreateDateDesc_oneUserSomeDifferentNotifications() {
        User sourceUser = a(user());
        User destinationUser = a(user());

        userDao.save(aListWith(sourceUser, destinationUser));
        Tweet tweetOne = a(tweet()
                .withOwner(sourceUser)
        );
        Tweet tweetTwo = a(tweet()
                .withOwner(sourceUser)
        );
        tweetDao.save(aListWith(tweetOne, tweetTwo));

        Notification notificationOne = a(notification()
                .withAbstractPost(tweetOne)
                .withSourceUser(sourceUser)
                .withDestinationUser(destinationUser)
                .withSeen(true)
        );

        Notification notificationTwo = a(notification()
                .withAbstractPost(tweetTwo)
                .withSourceUser(sourceUser)
                .withDestinationUser(destinationUser)
                .withSeen(false)
        );
        notificationDao.save(aListWith(notificationOne, notificationTwo));

        List<Notification> userDestinationNotifications = notificationDao.findByDestinationUserAndSeenOrderByCreateDateDesc(destinationUser, true, TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userDestinationNotifications, hasSize(1));
        assertThat(userDestinationNotifications, hasItem(notificationOne));
        assertTrue(userDestinationNotifications.stream().allMatch(notification -> notification.getDestinationUser().equals(destinationUser)));
    }

}
