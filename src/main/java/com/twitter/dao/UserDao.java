package com.twitter.dao;

import com.twitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mariusz on 12.07.16.
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

}
