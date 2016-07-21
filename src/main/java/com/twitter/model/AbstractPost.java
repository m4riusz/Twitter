package com.twitter.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariusz on 21.07.16.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractPost extends AbstractEntity {
    @NotNull
    private boolean banned;
    @NotNull
    private String content;
    @NotNull
    @ManyToOne
    private User owner;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserVote> votes;
    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    public AbstractPost() {
        super();
        this.banned = false;
        this.votes = new ArrayList<>();
        this.reports = new ArrayList<>();
    }

    public AbstractPost(String content, User owner) {
        this();
        this.content = content;
        this.owner = owner;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPost)) return false;
        if (!super.equals(o)) return false;

        AbstractPost that = (AbstractPost) o;

        if (!content.equals(that.content)) return false;
        return owner.equals(that.owner);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }
}
