package com.twitter.dao;

import com.twitter.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mariusz on 18.08.16.
 */
@Repository
public interface TagDao extends JpaRepository<Tag, Long> {

    Tag findByText(String text);

    // TODO: 15.12.16 add tests
    List<Tag> findByTextContainingIgnoreCase(String tagText, Pageable pageable);
}
