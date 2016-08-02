package com.twitter.util;

import com.twitter.model.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.builders.TagBuilder.tag;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Created by mariusz on 02.08.16.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TagExtractorTest {

    @Autowired
    private TagExtractor tagExtractor;


    @Test
    public void extract_contentFilledWithTag() {
        String content = "#someTag";
        List<Tag> extractedTags = tagExtractor.extract(content);
        assertThat(extractedTags, hasItem(a(tag().withText("someTag"))));
    }

    @Test
    public void extract_shouldReturnEmptyList() {
        String content = "#tag#tag # #tag# ##tag";
        List<Tag> extractedTags = tagExtractor.extract(content);
        assertThat(extractedTags, is(emptyList()));
    }

    @Test
    public void extract_containsWrongSymbol() {
        String content = "#af. #f/ #sfa? #faaf< #fsa> #ga, #a@ #5% #6^ #as& #as* #ag( #tag) #tag_ #tag- #tag= #tag+ #ta! #ta~ #tg`";
        List<Tag> extractedTags = tagExtractor.extract(content);
        assertThat(extractedTags, is(emptyList()));
    }

    @Test
    public void extract_duplicateTags() {
        String content = "#tag some random words #tag";
        List<Tag> extractedTags = tagExtractor.extract(content);
        assertThat(extractedTags, hasItem(a(tag().withText("tag"))));
        assertThat(extractedTags, hasSize(1));
    }

    @Test
    public void extract_textWithSomeTags() {
        Tag tagTopKek = a(tag().withText("topKek"));
        Tag tagTrueStory = a(tag().withText("trueStory"));
        Tag tagXD = a(tag().withText("xD"));
        String content = "Asad #topKek aasf #trueStory fask#xD j kj#xDnkj nkj njk njkn #xD jknjknjkn #xD h hasbdhjasd jjsa";
        List<Tag> tagList = tagExtractor.extract(content);
        assertThat(tagList, hasSize(3));
        assertThat(tagList, hasItems(tagTopKek, tagTrueStory, tagXD));
    }

}
