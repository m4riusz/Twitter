package com.twitter.builders;

import com.twitter.Builder;
import com.twitter.model.*;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 23.07.16.
 */
public final class ReportBuilder implements Builder<Report> {
    private static long counter = 0L;
    private ReportStatus status = ReportStatus.WAITING_FOR_REALIZATION;
    private ReportCategory category = ReportCategory.OTHER;
    private String message = "report content nr" + counter;
    private User user;
    private User judge;
    private long id;
    private Date createDate = Calendar.getInstance().getTime();
    private AbstractPost abstractPost;
    private int version;

    public static ReportBuilder report() {
        counter++;
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

    public ReportBuilder withAbstractPost(AbstractPost abstractPost) {
        this.abstractPost = abstractPost;
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
        report.setAbstractPost(abstractPost);
        report.setMessage(message);
        report.setUser(user);
        report.setJudge(judge);
        report.setId(id);
        report.setCreateDate(createDate);
        report.setVersion(version);
        return report;
    }
}
