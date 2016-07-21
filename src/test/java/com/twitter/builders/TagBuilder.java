package com.twitter.builders;

import com.twitter.model.Tag;

import java.util.Date;

/**
 * Created by mariusz on 21.07.16.
 */
public final class TagBuilder {
    private String text;
    private long id;
    private Date createDate;

    private TagBuilder() {
    }

    public static TagBuilder aTag() {
        return new TagBuilder();
    }

    public TagBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public TagBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public TagBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Tag build() {
        Tag tag = new Tag();
        tag.setText(text);
        tag.setId(id);
        tag.setCreateDate(createDate);
        return tag;
    }
}
