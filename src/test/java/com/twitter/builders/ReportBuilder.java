package com.twitter.builders;

import com.twitter.Builder;
import com.twitter.model.Report;
import com.twitter.model.ReportCategory;
import com.twitter.model.ReportStatus;
import com.twitter.model.User;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 23.07.16.
 */
public final class ReportBuilder implements Builder<Report> {
    private ReportStatus status = ReportStatus.WAITING_FOR_REALIZATION;
    private ReportCategory category = ReportCategory.OTHER;
    private String message = "report content";
    private User user;
    private User judge;
    private long id;
    private Date createDate = Calendar.getInstance().getTime();
    private int version;

    private ReportBuilder() {
    }

    public static ReportBuilder report() {
        return new ReportBuilder();
    }

    public ReportBuilder withStatus(ReportStatus status) {
        this.status = status;
        return this;
    }

    public ReportBuilder withCategory(ReportCategory category) {
        this.category = category;
        return this;
    }

    public ReportBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public ReportBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public ReportBuilder withJudge(User judge) {
        this.judge = judge;
        return this;
    }

    public ReportBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public ReportBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public ReportBuilder withVersion(int version) {
        this.version = version;
        return this;
    }

    public Report build() {
        Report report = new Report();
        report.setStatus(status);
        report.setCategory(category);
        report.setMessage(message);
        report.setUser(user);
        report.setJudge(judge);
        report.setId(id);
        report.setCreateDate(createDate);
        report.setVersion(version);
        return report;
    }
}