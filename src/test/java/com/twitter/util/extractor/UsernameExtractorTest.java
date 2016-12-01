package com.twitter.util.extractor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Created by mariusz on 01.12.16.
 */
@RunWith(JUnit4.class)
public class UsernameExtractorTest {

    private UsernameExtractor usernameExtractor;

    @Before
    public void setUp() {
        usernameExtractor = new SimpleUsernameExtractor();
    }


    @Test
    public void extract_shouldReturnEmptyList() {
        String content = "@user@user1 @ @us@ @@user";
        List<String> extractedStrings = usernameExtractor.extract(content);
        assertThat(extractedStrings, is(emptyList()));
    }

    @Test
    public void extract_containsWrongSymbol() {
        String content = "@af. @f/ @sfa? @faaf< @fsa> @ga, @a@ @5% @6^ @as& @as* @ag( @tag) @tag_ @tag- @tag= @tag+ @ta! @ta~ @tg`";
        List<String> extractedStrings = usernameExtractor.extract(content);
        assertThat(extractedStrings, is(emptyList()));
    }

    @Test
    public void extract_duplicateStrings() {
        String content = "@User some random words @User";
        List<String> extractedStrings = usernameExtractor.extract(content);
        assertThat(extractedStrings, hasItem("User"));
        assertThat(extractedStrings, hasSize(1));
    }

    @Test
    public void extract_textWithSomeStrings() {
        String userOne = "topKek";
        String userTwo = "trueStory";
        String userThree = "xD";
        String content = "Asad @topKek aasf @trueStory fask@xD j kj@xDnkj nkj njk njkn @xD jknjknjkn @xD h hasbdhjasd jjsa";
        List<String> tagList = usernameExtractor.extract(content);
        assertThat(tagList, hasSize(3));
        assertThat(tagList, hasItems(userOne, userTwo, userThree));
    }
}
