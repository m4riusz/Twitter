package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.Tag;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * Created by mariusz on 19.08.16.
 */
@SpringBootTest
@RunWith(value = SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TagDaoTest {

    @Autowired
    private TagDao tagDao;

    @Test
    public void findByText_tagDoesNotExist() {
        Tag tag = a(tag());
        Tag tagFound = tagDao.findByText(tag.getText());
        assertThat(tagFound, is(nullValue()));
    }

    @Test
    public void findByText_tagExists() {
        Tag tag = a(tag());
        tagDao.save(tag);
        Tag tagFound = tagDao.findByText(tag.getText());
        assertThat(tagFound, is(tag));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_noText() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, hasItems(tagOne, tagTwo));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_tagWitTextDoesNotExist() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("TAG_THREE", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, is(emptyList()));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_tagWithTextExists_fullMatch() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("TAG_ONE", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, contains(tagOne));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_tagWithTextExists_partMatch() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("TAG_", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, hasItems(tagOne, tagTwo));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_tagWithTextExists_caseTest_fullMatch() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("tag_one", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, contains(tagOne));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_tagWithTextExists_caseTest_partMatch() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("tag", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, hasItems(tagOne, tagTwo));
    }

    @Test
    public void findByTextStartingWithIgnoreCase_failSearch() {
        Tag tagOne = a(tag().withText("TAG_ONE"));
        Tag tagTwo = a(tag().withText("TAG_TWO"));
        tagDao.save(aListWith(tagOne, tagTwo));
        List<Tag> tagsFound = tagDao.findByTextStartingWithIgnoreCase("atag", TestUtil.ALL_IN_ONE_PAGE);
        assertThat(tagsFound, is(emptyList()));
    }

}
