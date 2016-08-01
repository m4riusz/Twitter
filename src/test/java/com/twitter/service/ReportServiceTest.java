package com.twitter.service;

import com.twitter.dao.ReportDao;
import com.twitter.exception.ReportException;
import com.twitter.exception.ReportNotFoundException;
import com.twitter.model.*;
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
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.ResultIsSuccessMatcher.hasFinishedSuccessfully;
import static com.twitter.matchers.ResultValueMatcher.hasValueOf;
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

    private ReportService reportService;

    @Before
    public void setUp() {
        reportService = new ReportServiceImpl(reportDao);
    }

    @Test(expected = ReportNotFoundException.class)
    public void findById_reportDoesNotExist() {
        when(reportDao.exists(anyLong())).thenReturn(false);
        reportService.findById(TestUtil.ID_ONE);
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
        assertThat(createReportResult, hasValueOf(Boolean.TRUE));
    }

    @Test(expected = ReportNotFoundException.class)
    public void judgeReport_reportDoesNotExist() {
        when(reportDao.exists(anyLong())).thenReturn(false);
        reportService.judgeReport(1L, ReportStatus.GUILTY, null, null);
    }

    @Test(expected = ReportException.class)
    public void judgeReport_userIsGuiltyAndDataIsNotSer() {
        Report report = a(report());
        when(reportDao.findOne(anyLong())).thenReturn(report);
        User judge = a(user());
        reportService.judgeReport(report.getId(), ReportStatus.GUILTY, judge, null);
    }

    @Test(expected = ReportException.class)
    public void judgeReport_userIsGuiltyAndDataIsInvalid() {
        Report report = a(report());
        when(reportDao.findOne(anyLong())).thenReturn(report);
        User judge = a(user());
        Date dateBeforeNow = DateTime.now().minusDays(1).toDate();
        reportService.judgeReport(report.getId(), ReportStatus.GUILTY, judge, dateBeforeNow);
    }

    @Test
    public void judgeReport_userIsGuilty() {
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
        when(reportDao.findOne(report.getId())).thenReturn(report);

        Date dateWhenBanExpired = DateTime.now().plusDays(2).toDate();
        Result<Boolean> judgeReportResult = reportService.judgeReport(
                report.getId(),
                ReportStatus.GUILTY,
                judge,
                dateWhenBanExpired
        );
        assertThat(judgeReportResult, hasFinishedSuccessfully());
        assertThat(judgeReportResult, hasValueOf(Boolean.TRUE));
        assertThat(tweetOwner.isAccountNonLocked(), is(false));
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
    }

}
