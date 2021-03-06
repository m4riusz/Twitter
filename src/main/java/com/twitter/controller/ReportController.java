package com.twitter.controller;

import com.twitter.dto.ReportSentence;
import com.twitter.model.Report;
import com.twitter.model.ReportCategory;
import com.twitter.model.ReportStatus;
import com.twitter.route.Route;
import com.twitter.service.ReportService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction;

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
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
        return new ResponseEntity<>(reportService.createReport(report), HttpStatus.CREATED);
    }

    @RequestMapping(value = Route.REPORT_URL, method = RequestMethod.PUT)
    public ResponseEntity<Report> judgeReport(@Valid @RequestBody ReportSentence reportSentence) throws TemplateException, IOException, MessagingException {
        return new ResponseEntity<>(reportService.judgeReport(reportSentence), HttpStatus.OK);
    }

    @RequestMapping(value = Route.REPORT_BY_ID, method = RequestMethod.GET)
    public ResponseEntity<Report> getReportById(@PathVariable long reportId) {
        return new ResponseEntity<>(reportService.findById(reportId), HttpStatus.OK);
    }

    @RequestMapping(value = Route.REPORT_GET_LATEST, method = RequestMethod.GET)
    public ResponseEntity<List<Report>> findLatestReports(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(reportService.findLatestReports(new PageRequest(page, size, Direction.DESC, "createDate")), HttpStatus.OK);
    }

    @RequestMapping(value = Route.REPORT_GET_FROM_USER, method = RequestMethod.GET)
    public ResponseEntity<List<Report>> findReportsFromUser(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(reportService.findUserReports(new PageRequest(page, size, Direction.DESC, "createDate")), HttpStatus.OK);
    }

    @RequestMapping(value = Route.REPORT_GET_ALL_BY_STATUS, method = RequestMethod.GET)
    public ResponseEntity<List<Report>> findReportsByStatus(@PathVariable ReportStatus reportStatus, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(reportService.findLatestByStatus(reportStatus, new PageRequest(page, size, Direction.DESC, "createDate")), HttpStatus.OK);
    }

    @RequestMapping(value = Route.REPORT_GET_ALL_BY_CATEGORY, method = RequestMethod.GET)
    public ResponseEntity<List<Report>> findReportsByCategory(@PathVariable ReportCategory reportCategory, @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(reportService.findLatestByCategory(reportCategory, new PageRequest(page, size, Direction.DESC, "createDate")), HttpStatus.OK);
    }

    @RequestMapping(value = Route.REPORT_GET_ALL_BY_STATUS_AND_CATEGORY, method = RequestMethod.GET)
    public ResponseEntity<List<Report>> findReportsByStatusAndCategory(@PathVariable ReportStatus reportStatus,
                                                                       @PathVariable ReportCategory reportCategory,
                                                                       @PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(reportService.findLatestByStatusAndCategory(reportStatus, reportCategory, new PageRequest(page, size, Direction.DESC, "createDate")), HttpStatus.OK);
    }

}
