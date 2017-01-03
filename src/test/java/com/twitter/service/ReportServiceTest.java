package com.twitter.service;

import com.twitter.config.Profiles;
import com.twitter.dao.ReportDao;
import com.twitter.dto.ReportSentence;
import com.twitter.exception.ReportAlreadyExist;
import com.twitter.exception.ReportNotFoundException;
import com.twitter.exception.TwitterDateException;
import com.twitter.model.*;
import com.twitter.util.MessageUtil;
import com.twitter.util.TestUtil;
import freemarker.template.TemplateException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.twitter.builders.AccountStatusBuilder.accountStatus;
import static com.twitter.builders.ReportBuilder.report;
import static com.twitter.builders.ReportSentenceBuilder.reportSentence;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.matchers.UserIsBanned.isBanned;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Created by mariusz on 01.08.16.
 */

@SpringBootTest
@ActiveProfiles(Profiles.DEV)
@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @Mock
    private ReportDao reportDao;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EmailService emailService;

    private ReportService reportService;

    @Before
    public void setUp() {
        reportService = new ReportServiceImpl(reportDao, userService, notificationService, emailService);
    }

    @Test(expected = ReportNotFoundException.class)
    public void findById_reportDoesNotExist() {
        when(reportDao.exists(anyLong())).thenReturn(false);
        reportService.findById(TestUtil.ID_ONE);
    }

    @Test
    public void findById_reportDoesExist() {
        User reportOwner = a(user());
        Tweet tweet = a(tweet().withOwner(a(user())));
        Report report = a(report().withUser(reportOwner).withAbstractPost(tweet));
        when(reportDao.exists(anyLong())).thenReturn(true);
        when(reportDao.findOne(anyLong())).thenReturn(report);
        Report reportFromDb = reportService.findById(TestUtil.ID_ONE);
        assertThat(reportFromDb, is(report));
    }

    @Test(expected = ReportAlreadyExist.class)
    public void createReport_reportAlreadyExists() {
        User accuser = a(user());
        Tweet tweet = a(tweet()
                .withOwner(a(user()))
        );
        Report report = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
        );
        when(userService.getCurrentLoggedUser()).thenReturn(accuser);
        when(reportDao.save(any(Report.class))).thenReturn(report);
        when(reportDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(Optional.of(report));
        reportService.createReport(report);
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
        when(userService.getCurrentLoggedUser()).thenReturn(accuser);
        when(reportDao.save(any(Report.class))).thenReturn(report);
        when(reportDao.findByUserAndAbstractPost(any(User.class), any(AbstractPost.class))).thenReturn(Optional.empty());
        Report savedReport = reportService.createReport(report);
        assertThat(savedReport, is(report));
        assertThat(savedReport.getAbstractPost(), is(tweet));
        assertThat(savedReport.getAbstractPost().getOwner(), is(tweetOwner));
    }

    @Test(expected = ReportNotFoundException.class)
    public void judgeReport_reportDoesNotExist() throws TemplateException, IOException, MessagingException {
        when(reportDao.exists(anyLong())).thenReturn(false);
        ReportSentence reportSentence = a(reportSentence()
                .withReportStatus(ReportStatus.GUILTY)
        );
        reportService.judgeReport(reportSentence);

    }

    @Test(expected = TwitterDateException.class)
    public void judgeReport_userIsGuiltyAndDataIsNotSet() throws TemplateException, IOException, MessagingException {
        User postOwner = a(user());
        User accuser = a(user());
        Tweet tweet = a(tweet()
                .withOwner(postOwner)
        );
        Report report = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
        );
        ReportSentence reportSentence = a(reportSentence()
                .withReportId(report.getId())
                .withReportStatus(ReportStatus.GUILTY)
                .withDateToBlock(null)
        );
        when(reportDao.exists(anyLong())).thenReturn(true);
        when(reportDao.findOne(anyLong())).thenReturn(report);
        reportService.judgeReport(reportSentence);
    }

    @Test(expected = TwitterDateException.class)
    public void judgeReport_userIsGuiltyAndDataIsInvalid() throws TemplateException, IOException, MessagingException {
        Report report = a(report());
        Date dateBeforeNow = DateTime.now().minusDays(1).toDate();
        ReportSentence reportSentence = a(reportSentence()
                .withReportId(report.getId())
                .withReportStatus(ReportStatus.GUILTY)
                .withDateToBlock(dateBeforeNow)
        );
        when(reportDao.exists(anyLong())).thenReturn(true);
        when(reportDao.findOne(anyLong())).thenReturn(report);
        reportService.judgeReport(reportSentence);
    }

    @Test
    public void judgeReport_bannedUserIsBannedAgainWithShortBanDate_banDateShouldNotBeShorten() throws TemplateException, IOException, MessagingException {
        Date newDateToBlock = DateTime.now().plusDays(7).toDate();
        Date oldBanDate = DateTime.now().plusDays(10).toDate();

        User tweetOwner = a(user()
                .withAccountStatus(a(accountStatus()
                                .withBannedUntil(oldBanDate)
                        )
                )

        );
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
                .withDateToBlock(newDateToBlock)
        );
        when(reportDao.exists(anyLong())).thenReturn(true);
        when(reportDao.findOne(anyLong())).thenReturn(report);
        when(userService.getCurrentLoggedUser()).thenReturn(judge);

        reportService.judgeReport(reportSentence);
        assertThat(tweetOwner, isBanned(true));
        assertThat(tweet.isBanned(), is(true));
        assertThat(tweetOwner.getAccountStatus().getBannedUntil(), is(oldBanDate));
        assertThat(tweet.getContent(), is(MessageUtil.DELETE_ABSTRACT_POST_CONTENT));
        assertThat(report.getJudge(), is(judge));
        assertThat(report.getStatus(), is(ReportStatus.GUILTY));
    }

    @Test
    public void judgeReport_bannedUserIsBannedAgainWithLongerBanDate_banDateShouldBeLonger() throws TemplateException, IOException, MessagingException {
        Date dateBeforeNewBanDate = DateTime.now().plusDays(7).toDate();
        Date newLongerBanDate = DateTime.now().plusDays(10).toDate();

        User tweetOwner = a(user()
                .withAccountStatus(a(accountStatus()
                                .withBannedUntil(dateBeforeNewBanDate)
                        )
                )

        );
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
                .withDateToBlock(newLongerBanDate)
        );
        when(reportDao.exists(anyLong())).thenReturn(true);
        when(reportDao.findOne(anyLong())).thenReturn(report);
        when(userService.getCurrentLoggedUser()).thenReturn(judge);

        reportService.judgeReport(reportSentence);
        assertThat(tweetOwner, isBanned(true));
        assertThat(tweet.isBanned(), is(true));
        assertThat(tweetOwner.getAccountStatus().getBannedUntil(), is(newLongerBanDate));
        assertThat(tweet.getContent(), is(MessageUtil.DELETE_ABSTRACT_POST_CONTENT));
        assertThat(report.getJudge(), is(judge));
        assertThat(report.getStatus(), is(ReportStatus.GUILTY));
    }

    @Test
    public void judgeReport_userIsGuilty() throws TemplateException, IOException, MessagingException {
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

        reportService.judgeReport(reportSentence);
        assertThat(tweetOwner, isBanned(true));
        assertThat(tweet.isBanned(), is(true));
        assertThat(tweet.getContent(), is(MessageUtil.DELETE_ABSTRACT_POST_CONTENT));
        assertThat(report.getJudge(), is(judge));
        assertThat(report.getStatus(), is(ReportStatus.GUILTY));
    }

    @Test
    public void findLatestByStatus_someReports() {
        Report reportOne = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Report reportTwo = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCreateDate(TestUtil.DATE_2002)
        );
        Report reportThree = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCreateDate(TestUtil.DATE_2001)
        );
        when(reportDao.findByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE)
        ).thenReturn(aListWith(reportOne, reportTwo, reportThree));

        List<Report> latestReportsByStatusResult = reportService.findLatestByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReportsByStatusResult, contains(reportOne, reportTwo, reportThree));
    }

    @Test
    public void findLatestByCategory_someReports() {
        Report reportOne = a(report()
                .withCategory(ReportCategory.HATE_SPEECH)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Report reportTwo = a(report()
                .withCategory(ReportCategory.HATE_SPEECH)
                .withCreateDate(TestUtil.DATE_2002)
        );
        Report reportThree = a(report()
                .withCategory(ReportCategory.HATE_SPEECH)
                .withCreateDate(TestUtil.DATE_2001)
        );
        when(reportDao.findByCategory(
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE)
        ).thenReturn(aListWith(reportOne, reportTwo, reportThree));

        List<Report> latestReportsByStatusResult = reportService.findLatestByCategory(
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReportsByStatusResult, contains(reportOne, reportTwo, reportThree));
    }

    @Test
    public void findLatestByStatusAndCategory_someReports() {
        Report reportOne = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.HATE_SPEECH)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Report reportTwo = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.HATE_SPEECH)
                .withCreateDate(TestUtil.DATE_2002)
        );
        Report reportThree = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.HATE_SPEECH)
                .withCreateDate(TestUtil.DATE_2001)
        );

        when(reportDao.findByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE)
        ).thenReturn(aListWith(reportOne, reportTwo, reportThree));

        List<Report> latestReportsByStatusResult = reportService.findLatestByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReportsByStatusResult, is(aListWith(reportOne, reportTwo, reportThree)));
    }

    @Test
    public void findUserReports_noReports() {
        User user = a(user());
        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(reportDao.findByUser(any(User.class), any(Pageable.class))).thenReturn(emptyList());
        List<Report> userReports = reportService.findUserReports(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userReports, is(emptyList()));
    }

    @Test
    public void findUserReports_someReports() {
        User user = a(user());

        Report reportOne = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.HATE_SPEECH)
                .withUser(user)
                .withCreateDate(TestUtil.DATE_2003)
        );
        Report reportTwo = a(report()
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.HATE_SPEECH)
                .withUser(user)
                .withCreateDate(TestUtil.DATE_2002)
        );

        when(userService.getCurrentLoggedUser()).thenReturn(user);
        when(reportDao.findByUser(user, TestUtil.ALL_IN_ONE_PAGE))
                .thenReturn(aListWith(reportOne, reportTwo));
        List<Report> userReports = reportService.findUserReports(TestUtil.ALL_IN_ONE_PAGE);
        assertThat(userReports, hasSize(2));
        assertThat(userReports, is(aListWith(reportOne, reportTwo)));
    }

    @Test
    public void findLatestReports_someReports() {
        Report reportOne = a(report()
                .withCreateDate(TestUtil.DATE_2003)
        );
        Report reportTwo = a(report()
                .withCreateDate(TestUtil.DATE_2002)
        );
        Report reportThree = a(report()
                .withCreateDate(TestUtil.DATE_2001)
        );
        when(reportDao.findAll(TestUtil.ALL_IN_ONE_PAGE)).thenReturn(new PageImpl<>(aListWith(reportOne, reportTwo, reportThree)));

        List<Report> latestReports = reportService.findLatestReports(
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(latestReports, contains(reportOne, reportTwo, reportThree));
    }



}
