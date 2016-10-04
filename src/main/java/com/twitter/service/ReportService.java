package com.twitter.service;

import com.twitter.model.*;
import com.twitter.dto.ReportSentence;
import com.twitter.util.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    Report judgeReport(ReportSentence reportSentence);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestByStatus(ReportStatus reportStatus, Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestByCategory(ReportCategory reportCategory, Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    List<Report> findLatestByStatusAndCategory(ReportStatus reportStatus, ReportCategory reportCategory, Pageable pageable);

    // TODO: 04.10.16 add tests
    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    List<Report> findUserReports(Pageable pageable);

}
