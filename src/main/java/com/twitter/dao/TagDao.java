package com.twitter.dao;

import com.twitter.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mariusz on 18.08.16.
 */
@Repository
public interface TagDao extends JpaRepository<Tag, Long> {
    Tag findByText(String text); // TODO: 18.08.16 add tests
}
