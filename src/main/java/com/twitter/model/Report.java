package com.twitter.model;

import com.sun.istack.internal.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */
@Entity
public class Report extends AbstractEntity{
    @NotNull
    private ReportStatus status;
    @NotNull
    private ReportCategory category;
    @Nullable
    private String message;
    @NotNull
    @ManyToOne
    private User user;
    @Nullable
    @OneToOne
    private User judge;

    public Report() {
        super();
        this.status = ReportStatus.WAITING_FOR_REALIZATION;
    }

    public Report(ReportCategory category, String message, User user, User judge) {
        this();
        this.category = category;
        this.message = message;
        this.user = user;
        this.judge = judge;
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
}
