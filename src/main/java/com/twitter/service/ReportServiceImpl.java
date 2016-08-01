package com.twitter.service;

import com.twitter.dao.ReportDao;
import com.twitter.exception.ReportException;
import com.twitter.exception.ReportNotFoundException;
import com.twitter.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mariusz on 31.07.16.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {


    private ReportDao reportDao;

    @Autowired
    public ReportServiceImpl(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    @Override
    public Result<Report> findById(long reportId) {
        if (reportDao.exists(reportId)) {
            return new Result<>(true, reportDao.findOne(reportId));
        }
        throw new ReportNotFoundException(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> createReport(Report report) {
        report.setJudge(null);
        reportDao.save(report);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> judgeReport(long reportId, ReportStatus reportStatus, User judge, Date timeToBlock) {
        Report report = reportDao.findOne(reportId);
        if (report == null) {
            throw new ReportNotFoundException(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
        } else if (isGuiltyAndDateIsNotSet(reportStatus, timeToBlock)) {
            throw new ReportException(MessageUtil.REPORT_DATE_NOT_SET_ERROR_MSG);
        } else if (isGuiltyAndDateIsInvalid(reportStatus, timeToBlock)) {
            throw new ReportException(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG);
        } else if (isGuilty(reportStatus)) {
            report.getAbstractPost().getOwner().getAccountStatus().setBannedUntil(timeToBlock);
        }
        report.getAbstractPost().setContent(MessageUtil.DELETE_ABSTRACT_POST_CONTENT);
        report.setStatus(reportStatus);
        report.setJudge(judge);
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<List<Report>> findLatestByStatus(ReportStatus reportStatus, Pageable pageable) {
        return new Result<>(true, reportDao.findByStatusOrderByCreateDateAsc(reportStatus, pageable));
    }

    @Override
    public Result<List<Report>> findLatestByCategory(ReportCategory reportCategory, Pageable pageable) {
        return new Result<>(true, reportDao.findByCategoryOrderByCreateDateAsc(reportCategory, pageable));
    }

    @Override
    public Result<List<Report>> findLatestByStatusAndCategory(ReportStatus reportStatus, ReportCategory reportCategory, Pageable pageable) {
        return new Result<>(true, reportDao.findByStatusAndCategoryOrderByCreateDateAsc(reportStatus, reportCategory, pageable));
    }

    private boolean isGuilty(ReportStatus reportStatus) {
        return reportStatus == ReportStatus.GUILTY;
    }

    private boolean isGuiltyAndDateIsInvalid(ReportStatus reportStatus, Date timeToBlock) {
        return isGuilty(reportStatus) && timeToBlock.before(Calendar.getInstance().getTime());
    }

    private boolean isGuiltyAndDateIsNotSet(ReportStatus reportStatus, Date timeToBlock) {
        return isGuilty(reportStatus) && timeToBlock == null;
    }
}
