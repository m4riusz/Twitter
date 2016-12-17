package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.service.TagService;
import com.twitter.util.TestUtil;
import com.twitter.util.WithCustomMockUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 19.08.16.
 */

@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class TagServiceSecurityTest {

    @Autowired
    private TagService tagService;

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void getUserFavouriteTags_anonymousAccessDenied() {
        tagService.getUserFavouriteTags(TestUtil.ID_ONE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.ANONYMOUS)
    public void addFavouriteTag_anonymousAccessDenied() {
        tagService.addFavouriteTag(TestUtil.ID_ONE, a(tag()));
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(id = TestUtil.ID_TWO)
    public void addFavouriteTag_wrongUserAccessDenied() {
        tagService.addFavouriteTag(TestUtil.ID_ONE, a(tag()));
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.ANONYMOUS)
    public void removeTagFromFavouriteTags_anonymousAccessDenied() {
        tagService.removeTagFromFavouriteTags(TestUtil.ID_ONE, a(tag()));
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(id = TestUtil.ID_TWO)
    public void removeTagFromFavouriteTags_wrongUserAccessDenied() {
        tagService.removeTagFromFavouriteTags(TestUtil.ID_ONE, a(tag()));
    }


    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void getTagsFromText_anonymousAccessDenied() {
        tagService.getTagsFromText("some #text");
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void queryForTag_anonymousAccessDenied() {
        tagService.queryForTag("Tag", TestUtil.ALL_IN_ONE_PAGE);
    }
}
