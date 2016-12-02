package com.twitter.dao;

import com.twitter.model.Notification;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mariusz on 01.12.16.
 */

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinationUserAndSeenOrderByCreateDateDesc(User user,boolean seen, Pageable pageable);
}
