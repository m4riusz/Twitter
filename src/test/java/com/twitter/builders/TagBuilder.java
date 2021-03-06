package com.twitter.builders;

import com.twitter.model.Tag;
import com.twitter.util.Builder;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 21.07.16.
 */
public final class TagBuilder implements Builder<Tag> {
    private static long counter = 0L;
    private String text = "tagNumber" + counter;
    private long id;
    private Date createDate = Calendar.getInstance().getTime();

    public static TagBuilder tag() {
        counter++;
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
