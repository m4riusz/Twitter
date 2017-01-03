package com.twitter.service;

import com.twitter.dto.ReportSentence;
import com.twitter.model.Report;
import com.twitter.model.ReportCategory;
import com.twitter.model.ReportStatus;
import com.twitter.util.SecurityUtil;
import freemarker.template.TemplateException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Created by mariusz on 31.07.16.
 */
@Service
@PreAuthorize(SecurityUtil.AUTHENTICATED)
public interface ReportService {
    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Report findById(long reportId);

    @PreAuthorize(SecurityUtil.PERSONAL_REPORT)
    Report createReport(Report report);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Report judgeReport(ReportSentence reportSentence) throws TemplateException, IOException, MessagingException;

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestByStatus(ReportStatus reportStatus, Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestByCategory(ReportCategory reportCategory, Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestByStatusAndCategory(ReportStatus reportStatus, ReportCategory reportCategory, Pageable pageable);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    List<Report> findUserReports(Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestReports(Pageable pageable);

}
