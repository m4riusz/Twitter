package com.twitter.model;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 29.11.16.
 */
@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"abstract_post_id", "destination_user_id", "source_user_id"})
)
public class Notification extends AbstractEntity {

    @ManyToOne
    private User sourceUser;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User destinationUser;
    @NotNull
    private String text;
    @NotNull
    private boolean seen;
    @NotNull
    @ManyToOne
    private AbstractPost abstractPost;


    public UserDetails getSourceUser() {
        return sourceUser;
    }

    public UserDetails getDestinationUser() {
        return destinationUser;
    }

    public String getText() {
        return text;
    }

    public boolean hasBeenSeen() {
        return seen;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }

    public void setDestinationUser(User destinationUser) {
        this.destinationUser = destinationUser;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public AbstractPost getAbstractPost() {
        return abstractPost;
    }

    public void setAbstractPost(AbstractPost abstractPost) {
        this.abstractPost = abstractPost;
    }
}
