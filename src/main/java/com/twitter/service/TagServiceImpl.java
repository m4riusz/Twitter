package com.twitter.service;

import com.twitter.dao.Query;
import com.twitter.dao.TagDao;
import com.twitter.exception.UserException;
import com.twitter.model.Tag;
import com.twitter.model.User;
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
    public static final String SPACE = " ";
    public static final String HASH = "#";

    private UserService userService;

    @Autowired
    public TagServiceImpl(TagDao tagDao, UserService userService) {
        this.tagDao = tagDao;
        this.userService = userService;
    }

    @Override
    public boolean exists(String text) {
        return tagDao.findByText(text) != null;
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
            throw new UserException(Query.TAG_ALREADY_IN_FAVOURITES_ERROR_MSG);
        } else if (tagFromDb == null) {
            tagFromDb = new Tag(tag.getText());
        }
        currentLoggedUser.getFavouriteTags().add(tagFromDb);
        return tagFromDb;
    }

    @Override
    public List<Tag> extract(String string) {
        String[] words = string.split(SPACE);
        List<String> tags = new ArrayList<>();
        List<Tag> tagList = new ArrayList<>();

        for (String word : words) {
            if (correctHash(word) && correctLengthAndFormat(word) && firstOccurrence(tags, word)) {
                String wordWithoutHash = word.substring(1);
                tags.add(wordWithoutHash);
                Tag tagFromDb = tagDao.findByText(wordWithoutHash);
                if (tagFromDb != null) {
                    tagList.add(tagFromDb);
                } else {
                    tagList.add(new Tag(wordWithoutHash));
                }
            }
        }
        return tagList;
    }

    private boolean correctLengthAndFormat(String word) {
        return hasEnoughLength(word) && containsOnlyDigsAndLetters(word.substring(1));
    }

    private boolean correctHash(String word) {
        return beginWithHash(word) && containsOnlyOneHash(word);
    }

    private boolean containsOnlyDigsAndLetters(String substring) {
        for (char c : substring.toCharArray()) {
            if (!(Character.isLetter(c) || Character.isDigit(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean firstOccurrence(List<String> tags, String word) {
        return !tags.contains(word.substring(1));
    }

    private boolean containsOnlyOneHash(String word) {
        return !word.substring(1).contains(HASH);
    }

    private boolean hasEnoughLength(String word) {
        return word.length() > 1;
    }

    private boolean beginWithHash(String word) {
        return word.startsWith(HASH);
    }

}
