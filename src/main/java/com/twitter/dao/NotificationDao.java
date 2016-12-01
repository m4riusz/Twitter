package com.twitter.dao;

import com.twitter.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mariusz on 01.12.16.
 */

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {
}
