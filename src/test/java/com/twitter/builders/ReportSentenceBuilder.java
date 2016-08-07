package com.twitter.builders;

import com.twitter.model.ReportSentence;
import com.twitter.model.ReportStatus;

import java.util.Date;

/**
 * Created by mariusz on 07.08.16.
 */
public final class ReportSentenceBuilder {
    private long reportId;
    private ReportStatus reportStatus;
    private Date dateToBlock;

    private ReportSentenceBuilder() {
    }

    public static ReportSentenceBuilder aReportSentence() {
        return new ReportSentenceBuilder();
    }

    public ReportSentenceBuilder withReportId(long reportId) {
        this.reportId = reportId;
        return this;
    }

    public ReportSentenceBuilder withReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
        return this;
    }

    public ReportSentenceBuilder withDateToBlock(Date dateToBlock) {
        this.dateToBlock = dateToBlock;
        return this;
    }

    public ReportSentence build() {
        ReportSentence reportSentence = new ReportSentence();
        reportSentence.setReportId(reportId);
        reportSentence.setReportStatus(reportStatus);
        reportSentence.setDateToBlock(dateToBlock);
        return reportSentence;
    }
}
