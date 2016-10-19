package com.twitter.service;

import com.twitter.dao.TagDao;
import com.twitter.exception.UserException;
import com.twitter.model.Tag;
import com.twitter.model.User;
import com.twitter.util.MessageUtil;
import com.twitter.util.TagExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariusz on 18.08.16.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private TagDao tagDao;
    private UserService userService;
    private TagExtractor tagExtractor;

    @Autowired
    public TagServiceImpl(TagDao tagDao, TagExtractor tagExtractor, UserService userService) {
        this.tagDao = tagDao;
        this.tagExtractor = tagExtractor;
        this.userService = userService;
    }

    @Override
    public List<Tag> getUserFavouriteTags(long userId) {
        return userService.getUserById(userId).getFavouriteTags();
    }

    @Override
    public Tag addFavouriteTag(long userId, Tag tag) {
        User currentLoggedUser = userService.getUserById(userId);
        Tag tagFromDb = tagDao.findByText(tag.getText());
        if (tagFromDb != null && currentLoggedUser.getFavouriteTags().contains(tagFromDb)) {
            throw new UserException(MessageUtil.TAG_ALREADY_IN_FAVOURITES_ERROR_MSG);
        } else if (tagFromDb == null) {
            tagFromDb = new Tag(tag.getText());
        }
        currentLoggedUser.getFavouriteTags().add(tagFromDb);
        return tagFromDb;
    }

    @Override
    public void removeTagFromFavouriteTags(long userId, Tag tag) {
        User currentLoggedUser = userService.getUserById(userId);
        if (currentLoggedUser.getFavouriteTags().contains(tag)) {
            currentLoggedUser.getFavouriteTags().remove(tag);
            return;
        }
        throw new UserException(MessageUtil.TAG_NOT_IN_FAVOURITES_DELETE_ERROR_MSG);
    }

    @Override
    public List<Tag> getTagsFromText(String content) {
        List<Tag> rawTags = tagExtractor.extract(content);
        return udpdateTags(rawTags);
    }

    @Override
    public List<Tag> updateTagIds(List<Tag> tagList) {
        return udpdateTags(tagList);
    }

    private List<Tag> udpdateTags(List<Tag> tagList) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : tagList) {
            Tag currentTag = tagDao.findByText(tag.getText());
            if (currentTag != null) {
                tags.add(currentTag);
                continue;
            }
            tags.add(tag);
        }
        return tags;
    }
}
