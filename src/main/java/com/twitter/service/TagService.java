package com.twitter.service;

import com.twitter.model.Tag;
import com.twitter.util.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 18.08.16.
 */
@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface TagService {

    boolean exists(String text); // TODO: 18.08.16 add tests

    List<Tag> getUserFavouriteTags(long userId); // TODO: 18.08.16 add tests

    @PreAuthorize(SecurityUtil.PERSONAL_USAGE)
    Tag addFavouriteTag(long userId, Tag tag); // TODO: 18.08.16 add tests

    List<Tag> extract(String string); // TODO: 18.08.16 add tests

}
