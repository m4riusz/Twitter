package com.twitter.security;

import com.twitter.config.Profiles;
import com.twitter.model.ReportCategory;
import com.twitter.model.ReportStatus;
import com.twitter.service.ReportService;
import com.twitter.util.TestUtil;
import com.twitter.util.WithCustomMockUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.twitter.builders.ReportBuilder.report;
import static com.twitter.builders.ReportSentenceBuilder.reportSentence;
import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 14.08.16.
 */

@RunWith(SpringRunner.class)
@ActiveProfiles(Profiles.DEV)
@SpringBootTest
public class ReportServiceSecurityTest {

    @Autowired
    private ReportService reportService;

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void findById_anonymousAccessDenied() {
        reportService.findById(TestUtil.ID_ONE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.USER)
    public void findById_userAccessDenied() {
        reportService.findById(TestUtil.ID_ONE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.ANONYMOUS)
    public void createReport_anonymousAccessDenied() {
        reportService.createReport(a(report()
                        .withUser(a(user()
                                        .withId(2L)
                                )
                        )
                )
        );
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(id = TestUtil.ID_ONE, authorities = TestUtil.USER)
    public void createReport_wrongUserAccessDenied() {
        reportService.createReport(a(report()
                        .withUser(a(user()
                                        .withId(TestUtil.ID_TWO)
                                )
                        )
                )
        );
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void judgeReport_anonymousAccessDenied() {
        reportService.judgeReport(a(reportSentence()));
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.USER)
    public void judgeReport_userAccessDenied() {
        reportService.judgeReport(a(reportSentence()));
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void findLatestByStatus_anonymousAccessDenied() {
        reportService.findLatestByStatus(ReportStatus.WAITING_FOR_REALIZATION, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.USER)
    public void findLatestByStatus_userAccessDenied() {
        reportService.findLatestByStatus(ReportStatus.WAITING_FOR_REALIZATION, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void findLatestByCategory_anonymousAccessDenied() {
        reportService.findLatestByCategory(ReportCategory.ADVERTISEMENT, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.USER)
    public void findLatestByCategory_userAccessDenied() {
        reportService.findLatestByCategory(ReportCategory.ADVERTISEMENT, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithAnonymousUser
    public void findLatestByStatusAndCategory_anonymousAccessDenied() {
        reportService.findLatestByStatusAndCategory(ReportStatus.GUILTY, ReportCategory.ADVERTISEMENT, TestUtil.ALL_IN_ONE_PAGE);
    }

    @Test(expected = AccessDeniedException.class)
    @WithCustomMockUser(authorities = TestUtil.USER)
    public void findLatestByStatusAndCategory_userAccessDenied() {
        reportService.findLatestByStatusAndCategory(ReportStatus.GUILTY, ReportCategory.ADVERTISEMENT, TestUtil.ALL_IN_ONE_PAGE);
    }

}
