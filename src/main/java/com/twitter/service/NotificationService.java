package com.twitter.service;

import com.twitter.model.Notification;
import com.twitter.util.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Created by mariusz on 01.12.16.
 */

@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface NotificationService {

    // TODO: 01.12.16 add tests
    Notification save(Notification notification);
}
