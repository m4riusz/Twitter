package com.twitter.model;

/**
 * Created by mariusz on 11.07.16.
 */
public class Report {
    private int id;
    private ReportStatus status;
    private ReportCategory category;
    private String message;
    private User user;
    private User judge;

    public Report(ReportCategory category, String message, User user, User judge) {
        this.status = ReportStatus.WAITING_FOR_REALIZATION;
        this.category = category;
        this.message = message;
        this.user = user;
        this.judge = judge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public ReportCategory getCategory() {
        return category;
    }

    public void setCategory(ReportCategory category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getJudge() {
        return judge;
    }

    public void setJudge(User judge) {
        this.judge = judge;
    }
}
