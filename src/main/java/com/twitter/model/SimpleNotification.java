package com.twitter.model;

import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 29.11.16.
 */
public class SimpleNotification extends AbstractEntity implements Notification {

    private User sourceUser;
    @NotNull
    private User destinationUser;
    @NotNull
    private String text;
    @NotNull
    private boolean seen;


    @Override
    public UserDetails getSourceUser() {
        return sourceUser;
    }

    @Override
    public UserDetails getDestinationUser() {
        return destinationUser;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean hasBeenSeen() {
        return seen;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }

    public void setDestinationUser(User destinationUser) {
        this.destinationUser = destinationUser;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
