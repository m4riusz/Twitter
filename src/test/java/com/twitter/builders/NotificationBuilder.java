package com.twitter.builders;

import com.twitter.model.AbstractPost;
import com.twitter.model.Notification;
import com.twitter.model.User;
import com.twitter.util.Builder;
import com.twitter.util.MessageUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 01.12.16.
 */
public final class NotificationBuilder implements Builder<Notification> {
    private User sourceUser;
    private User destinationUser;
    private String text = MessageUtil.YOU_HAVE_BEEN_MENTIONED_MESSAGE;
    private boolean seen = false;
    private AbstractPost abstractPost;
    private long id;
    private Date createDate = Calendar.getInstance().getTime();
    private int version;

    private NotificationBuilder() {
    }

    public static NotificationBuilder notification() {
        return new NotificationBuilder();
    }

    public NotificationBuilder withSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
        return this;
    }

    public NotificationBuilder withDestinationUser(User destinationUser) {
        this.destinationUser = destinationUser;
        return this;
    }

    public NotificationBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public NotificationBuilder withSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public NotificationBuilder withAbstractPost(AbstractPost abstractPost) {
        this.abstractPost = abstractPost;
        return this;
    }

    public NotificationBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public NotificationBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public NotificationBuilder withVersion(int version) {
        this.version = version;
        return this;
    }

    public Notification build() {
        Notification notification = new Notification();
        notification.setSourceUser(sourceUser);
        notification.setDestinationUser(destinationUser);
        notification.setText(text);
        notification.setSeen(seen);
        notification.setAbstractPost(abstractPost);
        notification.setId(id);
        notification.setCreateDate(createDate);
        notification.setVersion(version);
        return notification;
    }
}
