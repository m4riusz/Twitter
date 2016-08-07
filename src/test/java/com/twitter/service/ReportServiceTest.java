package com.twitter.service;

import com.twitter.dao.ReportDao;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.ReportBuilder.report;
import static com.twitter.builders.ReportSentenceBuilder.reportSentence;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.ResultIsFailureMatcher.hasFailed;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultMessageMatcher.hasMessageOf;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
import static com.twitter.matchers.UserIsBanned.isBanned;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 01.08.16.
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @Mock
    private ReportDao reportDao;
    @Mock
    private UserService userService;

    private ReportService reportService;

    @Before
    public void setUp() {
        reportService = new ReportServiceImpl(reportDao, userService);
    }

    public void findById_reportDoesNotExist() {
        when(reportDao.exists(anyLong())).thenReturn(false);
        Result<Report> reportResult = reportService.findById(TestUtil.ID_ONE);
        assertThat(reportResult, hasFailed());
        assertThat(reportResult, hasValueOf(null));
        assertThat(reportResult, hasMessageOf(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG));
    }


    @Test
    public void findById_reportDoesExist() {
        when(reportDao.exists(anyLong())).thenReturn(true);
        User reportOwner = a(user());
        Tweet tweet = a(tweet().withOwner(a(user())));
        Report report = a(report().withUser(reportOwner).withAbstractPost(tweet));
        when(reportDao.findOne(anyLong())).thenReturn(report);
        Result<Report> reportResult = reportService.findById(TestUtil.ID_ONE);
        assertThat(reportResult, hasFinishedSuccessfully());
        assertThat(reportResult, hasValueOf(report));
        assertThat(reportResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void createReport_simpleReport() {
        User tweetOwner = a(user());
        User accuser = a(user());
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        Report report = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
        );
        when(reportDao.save(any(Report.class))).thenReturn(report);
        Result<Boolean> createReportResult = reportService.createReport(report);
        assertThat(createReportResult, hasFinishedSuccessfully());
        assertThat(createReportResult, hasValueOf(true));
        assertThat(createReportResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }


    public void judgeReport_reportDoesNotExist() {
        when(reportDao.exists(anyLong())).thenReturn(false);
        ReportSentence reportSentence = a(reportSentence()
                .withReportStatus(ReportStatus.GUILTY)
        );
        Result<Boolean> reportResult = reportService.judgeReport(reportSentence);
        assertThat(reportResult, hasFailed());
        assertThat(reportResult, hasValueOf(null));
        assertThat(reportResult, hasMessageOf(MessageUtil.REPORT_NOT_FOUND_BY_ID_ERROR_MSG));
    }

    public void judgeReport_userIsGuiltyAndDataIsNotSet() {
        Report report = a(report());
        ReportSentence reportSentence = a(reportSentence()
                .withReportId(report.getId())
                .withReportStatus(ReportStatus.GUILTY)
        );
        when(reportDao.findOne(anyLong())).thenReturn(report);
        Result<Boolean> reportResult = reportService.judgeReport(reportSentence);
        assertThat(reportResult, hasFailed());
        assertThat(reportResult, hasValueOf(null));
        assertThat(reportResult, hasMessageOf(MessageUtil.REPORT_DATE_NOT_SET_ERROR_MSG));
    }

    public void judgeReport_userIsGuiltyAndDataIsInvalid() {
        Report report = a(report());
        Date dateBeforeNow = DateTime.now().minusDays(1).toDate();
        ReportSentence reportSentence = a(reportSentence()
                .withReportId(report.getId())
                .withReportStatus(ReportStatus.GUILTY)
                .withDateToBlock(dateBeforeNow)
        );
        when(reportDao.findOne(anyLong())).thenReturn(report);
        Result<Boolean> reportResult = reportService.judgeReport(reportSentence);
        assertThat(reportResult, hasFailed());
        assertThat(reportResult, hasValueOf(null));
        assertThat(reportResult, hasMessageOf(MessageUtil.REPORT_DATE_IS_INVALID_ERROR_MSG));
    }

    @Test
    public void judgeReport_userIsGuilty() {
        Date dateWhenBanExpired = DateTime.now().plusDays(2).toDate();
        User tweetOwner = a(user());
        User accuser = a(user());
        User judge = a(user());
        Tweet tweet = a(tweet()
                .withOwner(tweetOwner)
        );
        Report report = a(report()
                .withUser(accuser)
                .withAbstractPost(tweet)
        );
        ReportSentence reportSentence = a(reportSentence()
                .withReportId(report.getId())
                .withReportStatus(ReportStatus.GUILTY)
                .withDateToBlock(dateWhenBanExpired)
        );
        when(reportDao.exists(anyLong())).thenReturn(true);
        when(reportDao.findOne(anyLong())).thenReturn(report);
        when(userService.getCurrentLoggedUser()).thenReturn(judge);
        
        Result<Boolean> judgeReportResult = reportService.judgeReport(reportSentence);
        assertThat(judgeReportResult, hasFinishedSuccessfully());
        assertThat(judgeReportResult, hasValueOf(true));
        assertThat(judgeReportResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
        assertThat(tweetOwner, isBanned(true));
        assertThat(tweet.isBanned(), is(true));
        assertThat(tweet.getContent(), is(MessageUtil.DELETE_ABSTRACT_POST_CONTENT));
        assertThat(report.getJudge(), is(judge));
    }

    @Test
    public void findLatestByStatus_someReports() {
        List<Report> reportList = aListWith(
                a(report()
                        .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                        .withCreateDate(TestUtil.DATE_2003)
                ),
                a(report()
                        .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                        .withCreateDate(TestUtil.DATE_2002)
                ),
                a(report()
                        .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                        .withCreateDate(TestUtil.DATE_2001)
                )
        );
        when(reportDao.findByStatusOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE)
        ).thenReturn(reportList);

        Result<List<Report>> latestReportsByStatusResult = reportService.findLatestByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReportsByStatusResult, hasFinishedSuccessfully());
        assertThat(latestReportsByStatusResult, hasValueOf(reportList));
        assertThat(latestReportsByStatusResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void findLatestByCategory_someReports() {
        List<Report> reportList = aListWith(
                a(report()
                        .withCategory(ReportCategory.HATE_SPEECH)
                        .withCreateDate(TestUtil.DATE_2003)
                ),
                a(report()
                        .withCategory(ReportCategory.HATE_SPEECH)
                        .withCreateDate(TestUtil.DATE_2002)
                ),
                a(report()
                        .withCategory(ReportCategory.HATE_SPEECH)
                        .withCreateDate(TestUtil.DATE_2001)
                )
        );
        when(reportDao.findByCategoryOrderByCreateDateAsc(
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE)
        ).thenReturn(reportList);

        Result<List<Report>> latestReportsByStatusResult = reportService.findLatestByCategory(
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReportsByStatusResult, hasFinishedSuccessfully());
        assertThat(latestReportsByStatusResult, hasValueOf(reportList));
        assertThat(latestReportsByStatusResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

    @Test
    public void findLatestByStatusAndCategory_someReports() {
        List<Report> reportList = aListWith(
                a(report()
                        .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                        .withCategory(ReportCategory.HATE_SPEECH)
                        .withCreateDate(TestUtil.DATE_2003)
                ),
                a(report()
                        .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                        .withCategory(ReportCategory.HATE_SPEECH)
                        .withCreateDate(TestUtil.DATE_2002)
                ),
                a(report()
                        .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                        .withCategory(ReportCategory.HATE_SPEECH)
                        .withCreateDate(TestUtil.DATE_2001)
                )
        );
        when(reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE)
        ).thenReturn(reportList);

        Result<List<Report>> latestReportsByStatusResult = reportService.findLatestByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReportsByStatusResult, hasFinishedSuccessfully());
        assertThat(latestReportsByStatusResult, hasValueOf(reportList));
        assertThat(latestReportsByStatusResult, hasMessageOf(MessageUtil.RESULT_SUCCESS_MESSAGE));
    }

}
