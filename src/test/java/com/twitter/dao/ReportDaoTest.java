package com.twitter.dao;

import com.twitter.config.Profiles;
import com.twitter.model.*;
import com.twitter.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static com.twitter.builders.ReportBuilder.report;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;
import static com.twitter.util.Util.aListWith;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.springframework.data.domain.Sort.Direction;

/**
 * Created by mariusz on 22.07.16.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.DEV)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReportDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TweetDao tweetDao;

    @Autowired
    private ReportDao reportDao;

    @Test
    public void findAllReports_noReports() {
        User tweetOwner = a(user());
        userDao.save(aListWith(tweetOwner));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        List<Report> reports = reportDao.findAll();
        assertThat(reports, is(emptyList()));
    }

    @Test
    public void findByStatus_tweetOwnerReportsHimself() {
        User tweetOwner = a(user());
        userDao.save(aListWith(tweetOwner));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report report = a(report()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(report));
    }

    @Test
    public void findByStatus_manyReportsManyUsers() {
        User tweetOwner = a(user());
        User reportUserOne = a(user());
        User reportUserTwo = a(user());
        userDao.save(aListWith(tweetOwner, reportUserOne, reportUserTwo));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report reportOne = a(report()
                .withAbstractPost(tweet)
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withUser(reportUserOne)
        );
        Report reportTwo = a(report()
                .withAbstractPost(tweet)
                .withStatus(ReportStatus.GUILTY)
                .withUser(reportUserTwo)
        );
        reportDao.save(aListWith(reportOne, reportTwo));
        List<Report> reportList = reportDao.findByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItems(reportOne));
        assertThat(reportList, not(hasItem(reportTwo)));
    }

    @Test
    public void findByStatus_pagingAndOrderTest() {
        User tweetOwner = a(user());
        User reportUserOne = a(user());
        User reportUserTwo = a(user());
        User reportUserThree = a(user());
        userDao.save(aListWith(tweetOwner, reportUserOne, reportUserTwo, reportUserThree));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));

        Report reportOne = a(report()
                .withAbstractPost(tweet)
                .withUser(reportUserOne)
                .withCreateDate(TestUtil.DATE_2000)
        );
        Report reportTwo = a(report()
                .withAbstractPost(tweet)
                .withUser(reportUserTwo)
                .withCreateDate(TestUtil.DATE_2001)
        );
        Report reportThree = a(report()
                .withAbstractPost(tweet)
                .withUser(reportUserThree)
                .withCreateDate(TestUtil.DATE_2002)
        );
        reportDao.save(aListWith(reportOne, reportTwo, reportThree));
        List<Report> reportListPageOne = reportDao.findByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                new PageRequest(0, 2, Direction.DESC, "createDate")
        );
        List<Report> reportListPageTwo = reportDao.findByStatus(
                ReportStatus.WAITING_FOR_REALIZATION,
                new PageRequest(1, 2, Direction.DESC, "createDate")
        );
        assertThat(reportListPageOne, contains(reportThree, reportTwo));
        assertThat(reportListPageTwo, contains(reportOne));
    }

    @Test
    public void findByCategory_oneReport() {
        User owner = a(user());
        User accuser = a(user());
        userDao.save(aListWith(owner, accuser));
        Tweet tweet = a(tweet().withOwner(owner));
        tweetDao.save(tweet);
        Report report = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
                .withCategory(ReportCategory.HATE_SPEECH)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByCategory(
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(report));
        assertThat(reportList, hasSize(1));
    }

    @Test
    public void findByCategory_someReportsWithDifferentCategories() {
        User owner = a(user());
        User accuserOne = a(user());
        User accuserTwo = a(user());
        User accuserThree = a(user());
        userDao.save(aListWith(owner, accuserOne,accuserTwo,accuserThree));
        Tweet tweet = a(tweet().withOwner(owner));
        tweetDao.save(tweet);
        Report reportOne = a(report()
                .withAbstractPost(tweet)
                .withUser(accuserOne)
                .withCategory(ReportCategory.HATE_SPEECH)
        );
        Report reportTwo = a(report()
                .withAbstractPost(tweet)
                .withUser(accuserTwo)
                .withCategory(ReportCategory.ADVERTISEMENT)
        );
        Report reportThree = a(report()
                .withAbstractPost(tweet)
                .withUser(accuserThree)
                .withCategory(ReportCategory.OTHER)
        );
        reportDao.save(aListWith(reportOne, reportTwo, reportThree));
        List<Report> reportList = reportDao.findByCategory(
                ReportCategory.OTHER,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(reportThree));
        assertThat(reportList, hasSize(1));
    }

    @Test
    public void findByCategory_pagingAndOrderTest() {
        User tweetOwner = a(user());
        User reportUserOne = a(user());
        User reportUserTwo = a(user());
        User reportUserThree = a(user());
        userDao.save(aListWith(tweetOwner, reportUserOne, reportUserTwo, reportUserThree));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report reportOne = a(report()
                .withAbstractPost(tweet)
                .withUser(tweetOwner)
                .withCreateDate(TestUtil.DATE_2000)
                .withCategory(ReportCategory.ADVERTISEMENT)
        );
        Report reportTwo = a(report()
                .withAbstractPost(tweet)
                .withUser(reportUserTwo)
                .withCreateDate(TestUtil.DATE_2001)
                .withCategory(ReportCategory.ADVERTISEMENT)
        );
        Report reportThree = a(report()
                .withAbstractPost(tweet)
                .withUser(reportUserThree)
                .withCreateDate(TestUtil.DATE_2002)
                .withCategory(ReportCategory.ADVERTISEMENT)
        );
        reportDao.save(aListWith(reportOne, reportTwo, reportThree));
        List<Report> reportListPageOne = reportDao.findByCategory(
                ReportCategory.ADVERTISEMENT,
                new PageRequest(0, 2, Direction.DESC, "createDate")
        );
        List<Report> reportListPageTwo = reportDao.findByCategory(
                ReportCategory.ADVERTISEMENT,
                new PageRequest(1, 2, Direction.DESC, "createDate")
        );
        assertThat(reportListPageOne, contains(reportThree, reportTwo));
        assertThat(reportListPageTwo, contains(reportOne));
    }

    @Test
    public void findByStatusAndCategory_oneReportWithGoodStatusAndGoodCategory() {
        User accuser = a(user());
        User tweetOwner = a(user());
        userDao.save(aListWith(accuser, tweetOwner));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report report = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.HATE_SPEECH)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(report));
    }

    @Test
    public void findByStatusAndCategory_oneReportWithGoodStatusAndBadCategory() {
        User tweetOwner = a(user());
        User accuser = a(user());
        userDao.save(aListWith(tweetOwner, accuser));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report report = a(report()
                .withUser(accuser)
                .withAbstractPost(tweet)
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
                .withCategory(ReportCategory.SPAM_OR_FLOOD)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, not(hasItem(report)));
    }

    @Test
    public void findByStatusAndCategory_oneReportWithBadStatusAndGoodCategory() {
        User tweetOwner = a(user());
        User accuser = a(user());
        userDao.save(aListWith(tweetOwner, accuser));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report report = a(report()
                .withUser(accuser)
                .withAbstractPost(tweet)
                .withStatus(ReportStatus.INNOCENT)
                .withCategory(ReportCategory.SPAM_OR_FLOOD)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByStatusAndCategory(
                ReportStatus.GUILTY,
                ReportCategory.SPAM_OR_FLOOD,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, not(hasItem(report)));
    }

    @Test
    public void findByStatusAndCategory_oneReportWithBadStatusAndBadCategory() {
        User tweetOwner = a(user());
        User accuser = a(user());
        userDao.save(aListWith(tweetOwner, accuser));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report report = a(report()
                .withUser(accuser)
                .withAbstractPost(tweet)
                .withStatus(ReportStatus.INNOCENT)
                .withCategory(ReportCategory.SPAM_OR_FLOOD)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByStatusAndCategory(
                ReportStatus.GUILTY,
                ReportCategory.ADVERTISEMENT,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, not(hasItem(report)));
    }

    @Test
    public void findByStatusAndCategory_pagingAndOrderTest() {
        User tweetOwner = a(user());
        User reportUserOne = a(user());
        User reportUserTwo = a(user());
        User reportUserThree = a(user());
        userDao.save(aListWith(tweetOwner, reportUserOne, reportUserTwo, reportUserThree));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report reportOne = a(report()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
                .withCreateDate(TestUtil.DATE_2000)
                .withCategory(ReportCategory.ADVERTISEMENT)
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
        );
        Report reportTwo = a(report()
                .withUser(reportUserTwo)
                .withAbstractPost(tweet)
                .withCreateDate(TestUtil.DATE_2001)
                .withCategory(ReportCategory.ADVERTISEMENT)
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
        );
        Report reportThree = a(report()
                .withUser(reportUserThree)
                .withAbstractPost(tweet)
                .withCreateDate(TestUtil.DATE_2002)
                .withCategory(ReportCategory.ADVERTISEMENT)
                .withStatus(ReportStatus.WAITING_FOR_REALIZATION)
        );
        reportDao.save(aListWith(reportOne, reportTwo, reportThree));

        List<Report> reportListPageOne = reportDao.findByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.ADVERTISEMENT, new PageRequest(0, 2, Direction.DESC, "createDate")
        );
        List<Report> reportListPageTwo = reportDao.findByStatusAndCategory(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.ADVERTISEMENT, new PageRequest(1, 2, Direction.DESC, "createDate")
        );
        assertThat(reportListPageOne, contains(reportThree, reportTwo));
        assertThat(reportListPageTwo, contains(reportOne));
    }

    @Test
    public void findByUser_oneUserNoReports() {
        User user = a(user());
        userDao.save(user);
        List<Report> userReports = reportDao.findByUser(user, new PageRequest(0, 10));
        assertThat(userReports, is(emptyList()));
    }

    @Test
    public void findByUser_oneUserSomeReports() {
        User user = a(user());
        User postOwner = a(user());
        userDao.save(aListWith(user, postOwner));

        Tweet tweetOne = a(tweet().withOwner(postOwner));
        Tweet tweetTwo = a(tweet().withOwner(postOwner));
        tweetDao.save(aListWith(tweetOne, tweetTwo));

        Report reportOne = a(report().withUser(user).withAbstractPost(tweetOne));
        Report reportTwo = a(report().withUser(user).withAbstractPost(tweetTwo));
        reportDao.save(aListWith(reportOne, reportTwo));

        List<Report> userReports = reportDao.findByUser(user, new PageRequest(0, 10));
        assertThat(userReports, hasItems(reportOne, reportTwo));
        assertThat(userReports, hasSize(2));
    }

    @Test
    public void findByUser_someUserSomeReports() {
        User userOne = a(user());
        User userTwo = a(user());
        User postOwner = a(user());
        userDao.save(aListWith(userOne, userTwo, postOwner));

        Tweet tweetOne = a(tweet().withOwner(postOwner));
        Tweet tweetTwo = a(tweet().withOwner(postOwner));
        Tweet tweetThree = a(tweet().withOwner(postOwner));
        Tweet tweetFour = a(tweet().withOwner(postOwner));
        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree, tweetFour));

        Report reportOne = a(report().withUser(userOne).withAbstractPost(tweetOne));
        Report reportTwo = a(report().withUser(userOne).withAbstractPost(tweetTwo));
        Report reportThree = a(report().withUser(userTwo).withAbstractPost(tweetThree));
        Report reportFour = a(report().withUser(userTwo).withAbstractPost(tweetFour));
        reportDao.save(aListWith(reportOne, reportTwo, reportThree, reportFour));

        List<Report> userOneReports = reportDao.findByUser(userOne, new PageRequest(0, 10));
        List<Report> userTwoReports = reportDao.findByUser(userTwo, new PageRequest(0, 10));
        assertThat(userOneReports, hasItems(reportOne, reportTwo));
        assertThat(userTwoReports, hasItems(reportThree, reportFour));
        assertThat(userOneReports, hasSize(2));
        assertThat(userTwoReports, hasSize(2));
    }

    @Test
    public void findByUser_orderTest() {
        User user = a(user());
        User postOwner = a(user());
        userDao.save(aListWith(user, postOwner));

        Tweet tweetOne = a(tweet().withOwner(postOwner));
        Tweet tweetTwo = a(tweet().withOwner(postOwner));
        Tweet tweetThree = a(tweet().withOwner(postOwner));

        tweetDao.save(aListWith(tweetOne, tweetTwo, tweetThree));

        Report reportOne = a(report()
                .withUser(user)
                .withAbstractPost(tweetOne)
                .withCreateDate(TestUtil.DATE_2000)
        );
        Report reportTwo = a(report()
                .withUser(user)
                .withAbstractPost(tweetTwo)
                .withCreateDate(TestUtil.DATE_2001)
        );
        Report reportThree = a(report()
                .withUser(user)
                .withAbstractPost(tweetThree)
                .withCreateDate(TestUtil.DATE_2002)
        );
        reportDao.save(aListWith(reportOne, reportTwo, reportThree));

        List<Report> userReportsPageOne = reportDao.findByUser(user, new PageRequest(0, 2, Direction.DESC, "createDate"));
        List<Report> userReportsPageTwo = reportDao.findByUser(user, new PageRequest(1, 2, Direction.DESC, "createDate"));
        assertThat(userReportsPageOne, contains(reportThree, reportTwo));
        assertThat(userReportsPageOne, hasSize(2));
        assertThat(userReportsPageTwo, contains(reportOne));
        assertThat(userReportsPageTwo, hasSize(1));

    }

    @Test
    public void findByUserAndAbstractPost_userDoesNotExist() {
        User user = a(user());
        userDao.save(user);
        Tweet tweet = a(tweet()
                .withOwner(user)
        );
        tweetDao.save(tweet);
        Optional<Report> optionalReport = reportDao.findByUserAndAbstractPost(null, tweet);
        assertFalse(optionalReport.isPresent());
    }

    @Test
    public void findByUserAndAbstractPost_postDoesNotExist() {
        User user = a(user());
        userDao.save(user);
        Optional<Report> optionalReport = reportDao.findByUserAndAbstractPost(user, null);
        assertFalse(optionalReport.isPresent());
    }

    @Test
    public void findByUserAndAbstractPost_reportDoesNotExist() {
        User user = a(user());
        User accuser = a(user());
        userDao.save(aListWith(user, accuser));
        Tweet tweet = a(tweet()
                .withOwner(accuser)
        );
        tweetDao.save(tweet);

        Optional<Report> optionalReport = reportDao.findByUserAndAbstractPost(user, tweet);
        assertFalse(optionalReport.isPresent());
    }

    @Test
    public void findByUserAndAbstractPost_reportExists() {
        User user = a(user());
        User accuser = a(user());
        userDao.save(aListWith(user, accuser));
        Tweet tweet = a(tweet()
                .withOwner(accuser)
        );
        tweetDao.save(tweet);

        Report report = a(report()
                .withAbstractPost(tweet)
                .withUser(user)
        );
        reportDao.save(report);
        Optional<Report> optionalReport = reportDao.findByUserAndAbstractPost(user, tweet);
        assertThat(optionalReport.get(), is(report));
    }

}
