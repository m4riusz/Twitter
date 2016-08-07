package com.twitter.model;

import com.sun.istack.internal.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by mariusz on 07.08.16.
 */
public class ReportSentence {
    @NotNull
    private long reportId;
    @NotNull
    private ReportStatus reportStatus;
    @Nullable
    private Date dateToBlock;

    public ReportSentence() {
    }


    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Date getDateToBlock() {
        return dateToBlock;
    }

    public void setDateToBlock(Date dateToBlock) {
        this.dateToBlock = dateToBlock;
    }
}
