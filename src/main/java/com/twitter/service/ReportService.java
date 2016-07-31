package com.twitter.service;

import com.twitter.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mariusz on 31.07.16.
 */
@Service
public interface ReportService {

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Report> findById(long reportId);

    @PreAuthorize(SecurityUtil.AUTHENTICATED)
    Result<Boolean> createReport(ReportCategory category, String message, User user, AbstractPost abstractPost);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<Boolean> judgeReport(long reportId, ReportStatus reportStatus, User judge);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<List<Report>> findLatestByStatus(ReportStatus reportStatus, Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<List<Report>> findLatestByCategory(ReportCategory reportCategory, Pageable pageable);

    @PreAuthorize(SecurityUtil.ADMIN_OR_MODERATOR)
    Result<List<Report>> findLatestByStatusAndCategory(ReportStatus reportStatus, ReportCategory reportCategory, Pageable pageable);

}
