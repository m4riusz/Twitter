package com.twitter.service;

import com.twitter.dao.ReportDao;
import com.twitter.exception.ReportNotFoundException;
import com.twitter.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mariusz on 31.07.16.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    public static final String REPORT_NOT_FOUND_BY_ID_ERROR_MSG = "Report with this id does not exist!";
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
        throw new ReportNotFoundException(REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> createReport(ReportCategory category, String message, User user, AbstractPost abstractPost) {
        reportDao.save(new Report(category, message, user, null, abstractPost));
        return new Result<>(true, Boolean.TRUE);
    }

    @Override
    public Result<Boolean> judgeReport(long reportId, ReportStatus reportStatus, User judge) {
        Report report = reportDao.findOne(reportId);
        if (report == null) {
            throw new ReportNotFoundException(REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
        }
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
}
