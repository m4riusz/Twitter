package com.twitter.dao;

import com.twitter.model.Report;
import com.twitter.model.ReportStatus;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import org.junit.Before;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
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

    @Before
    public void setUp() {
    }

    @Test
    public void findByStatusOrderByCreateDateAsc_noReports() {
        User accusedUser = a(user());
        userDao.save(aListWith(accusedUser));
        Tweet tweet = a(tweet().withOwner(accusedUser));
        tweetDao.save(aListWith(tweet));
        List<Report> reports = reportDao.findAll();
        assertThat(reports, is(emptyList()));
    }

    @Test
    public void findByStatusOrderByCreateDateAsc_oneReportOneUser() {
        User accusedUser = a(user());
        userDao.save(aListWith(accusedUser));
        Report report = a(report().withUser(accusedUser));
        Tweet tweet = a(tweet().withOwner(accusedUser).withReports(aListWith(report)));
        report.setAbstractPost(tweet);
        tweetDao.save(aListWith(tweet));
        List<Report> reportList = reportDao.findByStatusOrderByCreateDateAsc(ReportStatus.WAITING_FOR_REALIZATION, new PageRequest(0, 10));
        assertThat(reportList, hasItem(report));
    }

}
