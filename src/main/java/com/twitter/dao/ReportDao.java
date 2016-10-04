package com.twitter.dao;

import com.twitter.model.Report;
import com.twitter.model.ReportCategory;
import com.twitter.model.ReportStatus;
import com.twitter.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by mariusz on 22.07.16.
 */
@Repository
public interface ReportDao extends JpaRepository<Report, Long> {

    public List<Report> findByStatusOrderByCreateDateAsc(ReportStatus reportStatus, Pageable pageable);

    public List<Report> findByCategoryOrderByCreateDateAsc(ReportCategory reportCategory, Pageable pageable);

    public List<Report> findByStatusAndCategoryOrderByCreateDateAsc(ReportStatus status, ReportCategory reportCategory, Pageable pageable);

    // TODO: 04.10.16 add tests
    public List<Report> findByUser(User user, Pageable pageable);
}
