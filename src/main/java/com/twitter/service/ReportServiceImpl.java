package com.twitter.service;

import com.twitter.dao.ReportDao;
import com.twitter.dto.ReportSentence;
import com.twitter.exception.ReportAlreadyExist;
import com.twitter.exception.ReportNotFoundException;
import com.twitter.exception.TwitterDateException;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by mariusz on 31.07.16.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private ReportDao reportDao;
    private UserService userService;
    private NotificationService notificationService;

    @Autowired
    public ReportServiceImpl(ReportDao reportDao, UserService userService, NotificationService notificationService) {
        this.reportDao = reportDao;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public Report findById(long reportId) {
        if (doesReportExist(reportId)) {
            return reportDao.findOne(reportId);
        }
        throw new ReportNotFoundException(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
    }

    @Override
    public Report createReport(Report report) {
        report.setJudge(null);
        report.setUser(userService.getCurrentLoggedUser());
        Optional<Report> userReportOnPost = reportDao.findByUserAndAbstractPost(report.getUser(), report.getAbstractPost());
        userReportOnPost.ifPresent(rep -> {
            throw new ReportAlreadyExist(MessageUtil.REPORT_ALREADY_EXISTS);
        });
        return reportDao.save(report);
    }

    @Override
    public Report judgeReport(ReportSentence reportSentence) {
        Report reportFromDb = reportDao.findOne(reportSentence.getReportId());
        if (!doesReportExist(reportSentence.getReportId())) {
            throw new ReportNotFoundException(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG);
        } else if (isGuiltyAndDateIsNotSet(reportSentence)) {
            throw new TwitterDateException(MessageUtil.DATE_IS_NOT_SET);
        } else if (isGuiltyAndDateIsInvalid(reportSentence)) {
            throw new TwitterDateException(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG);
        } else if (isGuilty(reportSentence) && postOwnerIsNotBanned(reportFromDb)) {
            banPostAndPostOwner(reportSentence, reportFromDb);
        } else if (isGuilty(reportSentence) && newBanDateLastsLongerThanActualBanDate(reportSentence, reportFromDb)) {
            banPostAndPostOwner(reportSentence, reportFromDb);
        } else if (isGuilty(reportSentence) && !newBanDateLastsLongerThanActualBanDate(reportSentence, reportFromDb)) {
            banPost(reportFromDb);
        }

        setReportJudge(reportFromDb);
        updateReportStatus(reportSentence, reportFromDb);
        addUserNotification(reportFromDb.getUser(), reportFromDb.getJudge(), "Your report has been arbitrated!");
        return reportFromDb;
    }

    private void addUserNotification(User destination, User source, String text) {
        Notification notification = new Notification();
        notification.setSeen(false);
        notification.setDestinationUser(destination);
        notification.setSourceUser(source);
        notification.setText(text);
        notificationService.save(notification);
    }

    @Override
    public List<Report> findLatestByStatus(ReportStatus reportStatus, Pageable pageable) {
        return reportDao.findByStatus(reportStatus, pageable);
    }

    @Override
    public List<Report> findLatestByCategory(ReportCategory reportCategory, Pageable pageable) {
        return reportDao.findByCategory(reportCategory, pageable);
    }

    @Override
    public List<Report> findLatestByStatusAndCategory(ReportStatus reportStatus, ReportCategory reportCategory, Pageable pageable) {
        return reportDao.findByStatusAndCategory(reportStatus, reportCategory, pageable);
    }

    @Override
    public List<Report> findUserReports(Pageable pageable) {
        User user = userService.getCurrentLoggedUser();
        return reportDao.findByUser(user, pageable);
    }

    @Override
    public List<Report> findLatestReports(Pageable pageable) {
        return reportDao.findAll(pageable).getContent();
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

    private boolean doesReportExist(long reportId) {
        return reportDao.exists(reportId);
    }

    private void updateReportStatus(ReportSentence reportSentence, Report reportFromDb) {
        reportFromDb.setStatus(reportSentence.getReportStatus());
    }

    private void banPostAndPostOwner(ReportSentence reportSentence, Report reportFromDb) {
        setUserBanDate(reportSentence.getDateToBlock(), reportFromDb);
        banPost(reportFromDb);
    }

    private void setUserBanDate(Date date, Report reportFromDb) {
        userService.banUser(reportFromDb.getAbstractPost().getOwner().getId(), date);
    }

    private void setReportJudge(Report reportFromDb) {
        User judge = userService.getCurrentLoggedUser();
        reportFromDb.setJudge(judge);
    }

    private void banPost(Report report) {
        report.getAbstractPost().setBanned(true);
        report.getAbstractPost().setContent(MessageUtil.DELETE_ABSTRACT_POST_CONTENT);
    }

    private boolean newBanDateLastsLongerThanActualBanDate(ReportSentence reportSentence, Report reportFromDb) {
        return reportFromDb.getAbstractPost().getOwner().getAccountStatus().getBannedUntil().before(reportSentence.getDateToBlock());
    }

    private boolean postOwnerIsNotBanned(Report reportFromDb) {
        return reportFromDb.getAbstractPost().getOwner().getAccountStatus().getBannedUntil() == null;
    }

}
