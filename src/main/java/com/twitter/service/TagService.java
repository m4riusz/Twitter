package com.twitter.service;

import com.twitter.model.Tag;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 18.08.16.
 */
@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface TagService {

    List<Tag> getUserFavouriteTags(long userId);

    @PreAuthorize(SecurityUtil.PERSONAL_USAGE)
    Tag addFavouriteTag(long userId, Tag tag);

    @PreAuthorize(SecurityUtil.PERSONAL_USAGE)
    void removeTagFromFavouriteTags(long userId, Tag tag);

    List<Tag> getTagsFromText(String string);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    List<Tag> queryForTag(String tagText, Pageable pageable);

}
