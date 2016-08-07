package com.twitter.service;

import com.twitter.dao.ReportDao;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

import static com.twitter.model.Result.ResultFailure;
import static com.twitter.model.Result.ResultSuccess;

/**
 * Created by mariusz on 31.07.16.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private ReportDao reportDao;
    private UserService userService;

    @Autowired
    public ReportServiceImpl(ReportDao reportDao, UserService userService) {
        this.reportDao = reportDao;
        this.userService = userService;
    }

    @Override
    public Result<Report> findById(long reportId) {
        if (doesReportExist(reportId)) {
            return ResultSuccess(reportDao.findOne(reportId));
        }
        return ResultFailure(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
    }

    @Override
    public Result<Boolean> createReport(Report report) {
        try {
            report.setJudge(null);
            report.setUser(userService.getCurrentLoggedUser());
            reportDao.save(report);
            return ResultSuccess(true);
        } catch (Exception e) {
            return ResultFailure(e.getMessage());
        }
    }

    @Override
    public Result<Boolean> judgeReport(ReportSentence reportSentence) {
        Report reportFromDb = reportDao.findOne(reportSentence.getReportId());
        if (!doesReportExist(reportSentence.getReportId())) {
            return ResultFailure(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
        } else if (isGuiltyAndDateIsNotSet(reportSentence)) {
            return ResultFailure(MessageUtil.REPORT_DATE_NOT_SET_ERROR_MSG);
        } else if (isGuiltyAndDateIsInvalid(reportSentence)) {
            return ResultFailure(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG);
        } else if (isGuilty(reportSentence)) {
            banPostAndPostOwner(reportSentence, reportFromDb);
        }
        updateReportStatus(reportSentence, reportFromDb);
        return ResultSuccess(true);
    }

    @Override
    public Result<List<Report>> findLatestByStatus(ReportStatus reportStatus, Pageable pageable) {
        return ResultSuccess(reportDao.findByStatusOrderByCreateDateAsc(reportStatus, pageable));
    }

    @Override
    public Result<List<Report>> findLatestByCategory(ReportCategory reportCategory, Pageable pageable) {
        return ResultSuccess(reportDao.findByCategoryOrderByCreateDateAsc(reportCategory, pageable));
    }

    @Override
    public Result<List<Report>> findLatestByStatusAndCategory(ReportStatus reportStatus, ReportCategory reportCategory, Pageable pageable) {
        return ResultSuccess(reportDao.findByStatusAndCategoryOrderByCreateDateAsc(reportStatus, reportCategory, pageable));
    }

    private boolean isGuilty(ReportSentence reportSentence) {
        return reportSentence.getReportStatus() == ReportStatus.GUILTY;
    }

    private boolean isGuiltyAndDateIsInvalid(ReportSentence reportSentence) {
        return isGuilty(reportSentence) && reportSentence.getDateToBlock().before(Calendar.getInstance().getTime());
    }

    private boolean isGuiltyAndDateIsNotSet(ReportSentence reportSentence) {
        return isGuilty(reportSentence) && reportSentence.getDateToBlock() == null;
    }

    private boolean doesReportExist(long id) {
        return reportDao.exists(id);
    }

    private void updateReportStatus(ReportSentence reportSentence, Report reportFromDb) {
        reportFromDb.setStatus(reportSentence.getReportStatus());
    }

    private void banPostAndPostOwner(ReportSentence reportSentence, Report reportFromDb) {
        User judge = userService.getCurrentLoggedUser();
        reportFromDb.setJudge(judge);
        reportFromDb.getAbstractPost().getOwner().getAccountStatus().setBannedUntil(reportSentence.getDateToBlock());
        reportFromDb.getAbstractPost().setBanned(true);
        reportFromDb.getAbstractPost().setContent(MessageUtil.DELETE_ABSTRACT_POST_CONTENT);
    }

}
