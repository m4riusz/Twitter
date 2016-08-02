package com.twitter.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Tag extends AbstractEntity {

    @NotNull
    @Length(
            min = 1,
            max = 30,
            message = "Tag length should be between {min} and {max}!"
    )
    private String text;

    public Tag() {
        super();
    }

    public Tag(String text) {
        this();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return text.equals(tag.text);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
}
