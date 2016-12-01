package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.TagDao;
import com.twitter.exception.UserException;
import com.twitter.exception.UserNotFoundException;
import com.twitter.model.Tag;
import com.twitter.model.User;
import com.twitter.util.extractor.TagExtractor;
import com.twitter.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 19.08.16.
 */

@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {

    @Mock
    private TagDao tagDao;
    @Mock
    private TagExtractor tagExtractor;
    @Mock
    private UserService userService;

    private TagService tagService;

    @Before
    public void setUp() throws Exception {
        tagService = new TagServiceImpl(tagDao, tagExtractor, userService);
    }

    @Test(expected = UserNotFoundException.class)
    public void getUserFavouriteTags_userDoesNotExist() {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);
        tagService.getUserFavouriteTags(TestUtil.ID_ONE);
    }

    @Test
    public void getUserFavouriteTags_userExistsNoTags() {
        User user = a(user()
                .withFavouriteTags(
                        emptyList()
                )
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        List<Tag> userFavouriteTags = tagService.getUserFavouriteTags(user.getId());
        assertThat(userFavouriteTags, is(emptyList()));
    }

    @Test
    public void getUserFavouriteTags_userExistsSomeTags() {
        Tag tagOne = a(tag());
        Tag tagTwo = a(tag());
        User user = a(user()
                .withFavouriteTags(
                        aListWith(
                                tagOne,
                                tagTwo
                        )
                )
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        List<Tag> userFavouriteTags = tagService.getUserFavouriteTags(user.getId());
        assertThat(userFavouriteTags, hasItems(tagOne, tagTwo));
        assertThat(userFavouriteTags, hasSize(2));
    }

    @Test(expected = UserNotFoundException.class)
    public void addFavouriteTag_userDoesNotExist() {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);
        tagService.addFavouriteTag(TestUtil.ID_ONE, a(tag()));
    }

    @Test(expected = UserException.class)
    public void addFavouriteTag_tagAlreadyInFavouriteTags() {
        Tag tag = a(tag());
        User user = a(user()
                .withFavouriteTags(
                        aListWith(
                                tag
                        )
                )
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(tagDao.findByText(anyString())).thenReturn(tag);
        tagService.addFavouriteTag(user.getId(), tag);
    }

    @Test
    public void addFavouriteTag_userExists() {
        Tag tag = a(tag());
        User user = a(user()
                .withFavouriteTags(
                        aListWith()
                )
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        Tag addedTag = tagService.addFavouriteTag(user.getId(), tag);
        assertThat(addedTag, is(tag));
        assertThat(user.getFavouriteTags(), hasItem(tag));
    }

    @Test
    public void getTagsFromText_noTags() {
        when(tagExtractor.extract(anyString())).thenReturn(emptyList());
        List<Tag> tagsFromText = tagService.getTagsFromText("one two");
        assertThat(tagsFromText, is(emptyList()));
    }

    @Test
    public void getTagsFromText_oneNewTag() {
        Tag tag = a(tag()
                .withText("swag")
        );
        when(tagExtractor.extract(anyString())).thenReturn(aListWith(tag));
        when(tagDao.findByText(tag.getText())).thenReturn(null);
        List<Tag> tagsFromText = tagService.getTagsFromText("one two #swag");
        assertThat(tagsFromText, hasItem(tag));
    }

    @Test
    public void getTagsFromText_oneExistingTag() {
        Tag tag = a(tag()
                .withId(0L)
                .withText("swag")
        );
        Tag tagFromDb = a(tag()
                .withText("swag")
                .withId(1L)
        );
        when(tagExtractor.extract(anyString())).thenReturn(aListWith(tag));
        when(tagDao.findByText(tag.getText())).thenReturn(tagFromDb);
        List<Tag> tagsFromText = tagService.getTagsFromText("one two #swag");
        assertThat(tagsFromText, hasItem(tagFromDb));
    }

    @Test(expected = UserNotFoundException.class)
    public void removeTagFromFavouriteTags_userDoesNotExist() {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);
        tagService.removeTagFromFavouriteTags(TestUtil.ID_ONE, a(tag()));
    }

    @Test(expected = UserException.class)
    public void removeTagFromFavouriteTags_userExistsTagIsNotInFavouriteTags() {
        Tag tag = a(tag());
        User user = a(user()
                .withFavouriteTags(
                        aListWith()
                )
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        tagService.removeTagFromFavouriteTags(TestUtil.ID_ONE, tag);
    }

    @Test
    public void removeTagFromFavouriteTags_userExistsTagIsInFavouriteTags() {
        Tag tag = a(tag());
        User user = a(user()
                .withFavouriteTags(
                        aListWith(
                                tag
                        )
                )
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        tagService.removeTagFromFavouriteTags(TestUtil.ID_ONE, tag);
        assertThat(user.getFavouriteTags(), not(hasItem(tag)));
    }


}
