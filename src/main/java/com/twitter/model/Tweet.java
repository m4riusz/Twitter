package com.twitter.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Tweet extends AbstractPost {

    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Tag> tags = new ArrayList<>();
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tweet")
    private List<Comment> comments = new ArrayList<>();

    public Tweet(String content, User owner) {
        super(content, owner);
    }

    public Tweet() {
        super();
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}
