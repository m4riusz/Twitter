package com.twitter.model;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by mariusz on 29.11.16.
 */
public interface Notification {

    UserDetails getSourceUser();

    UserDetails getDestinationUser();

    String getText();

    boolean hasBeenSeen();
}
