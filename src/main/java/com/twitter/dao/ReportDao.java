package com.twitter.dao;

import com.twitter.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by mariusz on 22.07.16.
 */
@Repository
public interface ReportDao extends JpaRepository<Report, Long> {

    List<Report> findByStatus(ReportStatus reportStatus, Pageable pageable);

    List<Report> findByCategory(ReportCategory reportCategory, Pageable pageable);

    List<Report> findByStatusAndCategory(ReportStatus status, ReportCategory reportCategory, Pageable pageable);

    List<Report> findByUser(User user, Pageable pageable);

    // TODO: 22.11.16 add tests
    Optional<Report> findByUserAndAbstractPost(User user, AbstractPost abstractPost);
}
