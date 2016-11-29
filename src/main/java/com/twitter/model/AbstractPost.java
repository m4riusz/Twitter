package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Created by mariusz on 21.07.16.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = Id.NAME,
        include = As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Tweet.class, name = "tweet"),
        @JsonSubTypes.Type(value = Comment.class, name = "comment")
})
public abstract class AbstractPost extends AbstractEntity {
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean deleted;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean banned;
    @NotNull
    @Length(
            min = 1, max = 100,
            message = "Post length should be between {min} and {max}!"
    )
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String content;
    @NotNull
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private User owner;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "abstractPost")
    @JsonIgnore
    private List<UserVote> votes;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "abstractPost")
    @JsonIgnore
    private List<Report> reports;
    @NotNull
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    public AbstractPost() {
        super();
        this.deleted = false;
        this.banned = false;
        this.votes = new ArrayList<>();
        this.reports = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public AbstractPost(String content, User owner) {
        this();
        this.content = content;
        this.owner = owner;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<UserVote> getVotes() {
        return votes;
    }

    public void setVotes(List<UserVote> votes) {
        this.votes = votes;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPost)) return false;
        if (!super.equals(o)) return false;

        AbstractPost that = (AbstractPost) o;

        return content.equals(that.content) && owner.equals(that.owner);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }
}
