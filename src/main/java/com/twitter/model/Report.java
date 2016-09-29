package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.Nullable;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Report extends AbstractEntity{
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ReportStatus status;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private ReportCategory category;
    @NotNull
    @Length(
            max = 100,
            message = "Report length should be smaller than {max}!"
    )
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String message;
    @NotNull
    @ManyToOne
    private User user;
    @Nullable
    @OneToOne
    private User judge;
    @NotNull
    @ManyToOne
    private AbstractPost abstractPost;

    public Report() {
        super();
        this.status = ReportStatus.WAITING_FOR_REALIZATION;
    }

    public Report(ReportCategory category, String message, User user, User judge, AbstractPost abstractPost) {
        this();
        this.category = category;
        this.message = message;
        this.user = user;
        this.judge = judge;
        this.abstractPost = abstractPost;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public ReportCategory getCategory() {
        return category;
    }

    public void setCategory(ReportCategory category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getJudge() {
        return judge;
    }

    public void setJudge(User judge) {
        this.judge = judge;
    }

    public AbstractPost getAbstractPost() {
        return abstractPost;
    }

    public void setAbstractPost(AbstractPost abstractPost) {
        this.abstractPost = abstractPost;
    }
}
