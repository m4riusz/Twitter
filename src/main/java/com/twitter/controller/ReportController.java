package com.twitter.controller;

import com.twitter.model.*;
import com.twitter.route.Route;
import com.twitter.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by mariusz on 07.08.16.
 */
@RestController
public class ReportController {

    private ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(value = Route.REPORT_URL, method = RequestMethod.POST)
    public Result<Boolean> createReport(@Valid @RequestBody Report report) {
        return reportService.createReport(report);
    }

    @RequestMapping(value = Route.REPORT_BY_ID, method = RequestMethod.GET)
    public Result<Report> getReportById(@PathVariable long reportId) {
        return reportService.findById(reportId);
    }

    @RequestMapping(value = Route.REPORT_GET_ALL_BY_STATUS, method = RequestMethod.GET)
    public Result<List<Report>> findReportsByStatus(@PathVariable ReportStatus reportStatus, @PathVariable int page, @PathVariable int size) {
        return reportService.findLatestByStatus(reportStatus, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.REPORT_GET_ALL_BY_CATEGORY, method = RequestMethod.GET)
    public Result<List<Report>> findReportsByCategory(@PathVariable ReportCategory reportCategory, @PathVariable int page, @PathVariable int size) {
        return reportService.findLatestByCategory(reportCategory, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.REPORT_GET_ALL_BY_STATUS_AND_CATEGORY, method = RequestMethod.GET)
    public Result<List<Report>> findReportsByStatusAndCategory(@PathVariable ReportStatus reportStatus,
                                                               @PathVariable ReportCategory reportCategory,
                                                               @PathVariable int page, @PathVariable int size) {
        return reportService.findLatestByStatusAndCategory(reportStatus, reportCategory, new PageRequest(page, size));
    }

    @RequestMapping(value = Route.REPORT_URL, method = RequestMethod.PUT)
    public Result<Boolean> judgeReport(@Valid @RequestBody ReportSentence reportSentence) {
        return reportService.judgeReport(reportSentence);
    }

}
