package com.twitter.builders;

import com.twitter.Builder;
import com.twitter.model.Tag;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 21.07.16.
 */
public final class TagBuilder implements Builder<Tag> {
    private String text = "";
    private long id = 0;
    private Date createDate = Calendar.getInstance().getTime();

    private TagBuilder() {
    }

    public static TagBuilder tag() {
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
