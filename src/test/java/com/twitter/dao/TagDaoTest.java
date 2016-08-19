package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.util.Util.a;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
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

}
