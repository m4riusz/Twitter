package com.twitter.service;

import org.joda.time.DateTime;
import org.springframework.data.domain.PageRequest;

import java.util.Date;

/**
 * Created by mariusz on 30.07.16.
 */
public class TestUtil {
    public static final long ID_ONE = 1L;
    public static final long ID_TWO = 2L;
    public static final PageRequest ALL_IN_ONE_PAGE = new PageRequest(0, 10);

    public static final Date DATE_2000 = DateTime.now().withYear(2000).toDate();
    public static final Date DATE_2001 = DateTime.now().withYear(2001).toDate();
    public static final Date DATE_2002 = DateTime.now().withYear(2002).toDate();
    public static final Date DATE_2003 = DateTime.now().withYear(2003).toDate();
}
