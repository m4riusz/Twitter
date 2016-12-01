package com.twitter.util.extractor;

import com.twitter.model.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static com.twitter.builders.TagBuilder.tag;
import static com.twitter.util.Util.a;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Created by mariusz on 02.08.16.
 */
@RunWith(JUnit4.class)
public class TagExtractorTest {

    private TagExtractor tagExtractor;

    @Before
    public void setUp() {
        tagExtractor = new SimpleTagExtractor();
    }

    @Test
    public void extract_contentFilledWithString() {
        String content = "#someString";
        List<Tag> extractedStrings = tagExtractor.extract(content);
        assertThat(extractedStrings, hasItem(a(tag().withText("someString"))));
    }

    @Test
    public void extract_shouldReturnEmptyList() {
        String content = "#tag#tag # #tag# ##tag";
        List<Tag> extractedStrings = tagExtractor.extract(content);
        assertThat(extractedStrings, is(emptyList()));
    }

    @Test
    public void extract_containsWrongSymbol() {
        String content = "#af. #f/ #sfa? #faaf< #fsa> #ga, #a# #5% #6^ #as& #as* #ag( #tag) #tag_ #tag- #tag= #tag+ #ta! #ta~ #tg`";
        List<Tag> extractedStrings = tagExtractor.extract(content);
        assertThat(extractedStrings, is(emptyList()));
    }

    @Test
    public void extract_duplicateStrings() {
        String content = "#tag some random words #tag";
        List<Tag> extractedStrings = tagExtractor.extract(content);
        assertThat(extractedStrings, hasItem(a(tag().withText("tag"))));
        assertThat(extractedStrings, hasSize(1));
    }

    @Test
    public void extract_textWithSomeStrings() {
        Tag tagTopKek = a(tag().withText("topKek"));
        Tag tagTrueStory = a(tag().withText("trueStory"));
        Tag tagXD = a(tag().withText("xD"));
        String content = "Asad #topKek aasf #trueStory fask#xD j kj#xDnkj nkj njk njkn #xD jknjknjkn #xD h hasbdhjasd jjsa";
        List<Tag> tagList = tagExtractor.extract(content);
        assertThat(tagList, hasSize(3));
        assertThat(tagList, hasItems(tagTopKek, tagTrueStory, tagXD));
    }

}
