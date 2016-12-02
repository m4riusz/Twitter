package com.twitter.model;

import org.hibernate.annotations.Check;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 29.11.16.
 */
@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"abstract_post_id", "destination_user_id", "source_user_id"})
)
@Check(constraints = "destination_user_id != source_user_id")
public class Notification extends AbstractEntity {

    @ManyToOne
    private User sourceUser;
    @NotNull
    @ManyToOne
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
