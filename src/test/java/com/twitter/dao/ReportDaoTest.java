package com.twitter.dao;

import com.twitter.model.*;
import com.twitter.service.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.twitter.Util.a;
import static com.twitter.Util.aListWith;
import static com.twitter.builders.ReportBuilder.report;
import static com.twitter.builders.TweetBuilder.tweet;
import static com.twitter.builders.UserBuilder.user;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by mariusz on 22.07.16.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
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
    public void findByStatusOrderByCreateDateAsc_tweetOwnerReportsHimself() {
        User tweetOwner = a(user());
        userDao.save(aListWith(tweetOwner));
        Tweet tweet = a(tweet().withOwner(tweetOwner));
        tweetDao.save(aListWith(tweet));
        Report report = a(report()
                .withUser(tweetOwner)
                .withAbstractPost(tweet)
        );
        reportDao.save(report);
        List<Report> reportList = reportDao.findByStatusOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(report));
    }

    @Test
    public void findByStatusOrderByCreateDateAsc_manyReportsManyUsers() {
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
        List<Report> reportList = reportDao.findByStatusOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItems(reportOne));
        assertThat(reportList, not(hasItem(reportTwo)));
    }

    @Test
    public void findByStatusOrderByCreateDateAsc_pagingAndOrderTest() {
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
        List<Report> reportListPageOne = reportDao.findByStatusOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                new PageRequest(0, 2)
        );
        List<Report> reportListPageTwo = reportDao.findByStatusOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                new PageRequest(1, 2)
        );
        assertThat(reportListPageOne, contains(reportOne, reportTwo));
        assertThat(reportListPageTwo, contains(reportThree));
    }

    @Test
    public void findByCategoryOrderByCreateDateAsc_oneReport() {
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
        List<Report> reportList = reportDao.findByCategoryOrderByCreateDateAsc(
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(report));
        assertThat(reportList, hasSize(1));
    }

    @Test
    public void findByCategoryOrderByCreateDateAsc_someReportsWithDifferentCategories() {
        User owner = a(user());
        User accuser = a(user());
        userDao.save(aListWith(owner, accuser));
        Tweet tweet = a(tweet().withOwner(owner));
        tweetDao.save(tweet);
        Report reportOne = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
                .withCategory(ReportCategory.HATE_SPEECH)
        );
        Report reportTwo = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
                .withCategory(ReportCategory.ADVERTISEMENT)
        );
        Report reportThree = a(report()
                .withAbstractPost(tweet)
                .withUser(accuser)
                .withCategory(ReportCategory.OTHER)
        );
        reportDao.save(aListWith(reportOne, reportTwo, reportThree));
        List<Report> reportList = reportDao.findByCategoryOrderByCreateDateAsc(
                ReportCategory.OTHER,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(reportThree));
        assertThat(reportList, hasSize(1));
    }

    @Test
    public void findByCategoryOrderByCreateDateAsc_pagingAndOrderTest() {
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
        List<Report> reportListPageOne = reportDao.findByCategoryOrderByCreateDateAsc(
                ReportCategory.ADVERTISEMENT,
                new PageRequest(0, 2)
        );
        List<Report> reportListPageTwo = reportDao.findByCategoryOrderByCreateDateAsc(
                ReportCategory.ADVERTISEMENT,
                new PageRequest(1, 2)
        );
        assertThat(reportListPageOne, contains(reportOne, reportTwo));
        assertThat(reportListPageTwo, contains(reportThree));
    }

    @Test
    public void findByStatusAndCategoryOrderByCreateDateAsc_oneReportWithGoodStatusAndGoodCategory() {
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
        List<Report> reportList = reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, hasItem(report));
    }

    @Test
    public void findByStatusAndCategoryOrderByCreateDateAsc_oneReportWithGoodStatusAndBadCategory() {
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
        List<Report> reportList = reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.HATE_SPEECH,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, not(hasItem(report)));
    }

    @Test
    public void findByStatusAndCategoryOrderByCreateDateAsc_oneReportWithBadStatusAndGoodCategory() {
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
        List<Report> reportList = reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.GUILTY,
                ReportCategory.SPAM_OR_FLOOD,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, not(hasItem(report)));
    }

    @Test
    public void findByStatusAndCategoryOrderByCreateDateAsc_oneReportWithBadStatusAndBadCategory() {
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
        List<Report> reportList = reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.GUILTY,
                ReportCategory.ADVERTISEMENT,
                TestUtil.ALL_IN_ONE_PAGE
        );
        assertThat(reportList, not(hasItem(report)));
    }

    @Test
    public void findByStatusAndCategoryOrderByCreateDateAsc_pagingAndOrderTest() {
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

        List<Report> reportListPageOne = reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.ADVERTISEMENT, new PageRequest(0, 2)
        );
        List<Report> reportListPageTwo = reportDao.findByStatusAndCategoryOrderByCreateDateAsc(
                ReportStatus.WAITING_FOR_REALIZATION,
                ReportCategory.ADVERTISEMENT, new PageRequest(1, 2)
        );
        assertThat(reportListPageOne, contains(reportOne, reportTwo));
        assertThat(reportListPageTwo, contains(reportThree));
    }
}
