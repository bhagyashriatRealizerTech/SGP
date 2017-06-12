package com.realizer.schoolgenie.parent.generalcommunication.model;

/**
 * Created by Win on 11/17/2015.
 */
public class ParentGeneralCommunicationListModel {

    private String category = "";
    private String announcementTime ="";
    private String sentby = "";
    private String schoolCode = "";
    private String announcementId = "";
    private String std ="";
    private String division = "";
    private String academicYr = "";
    private String announcementText = "";
    private String currentYear="";
    private String CurMonth="";
    private String curDay="";
    private String curHour="";
    private String curMin="";

    public String getCurSecond() {
        return curSecond;
    }

    public void setCurSecond(String curSecond) {
        this.curSecond = curSecond;
    }

    public String getCurMin() {
        return curMin;
    }

    public void setCurMin(String curMin) {
        this.curMin = curMin;
    }

    public String getCurHour() {
        return curHour;
    }

    public void setCurHour(String curHour) {
        this.curHour = curHour;
    }

    public String getCurDay() {
        return curDay;
    }

    public void setCurDay(String curDay) {
        this.curDay = curDay;
    }

    public String getCurMonth() {
        return CurMonth;
    }

    public void setCurMonth(String curMonth) {
        CurMonth = curMonth;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSentby() {
        return sentby;
    }

    public void setSentby(String sentby) {
        this.sentby = sentby;
    }

    private String curSecond;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAnnouncementTime() {
        return announcementTime;
    }

    public void setAnnouncementTime(String announcementTime) {
        this.announcementTime = announcementTime;
    }

    public String getsentBy() {
        return sentby;
    }

    public void setsentBy(String sentby) {
        this.sentby = sentby;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }
    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }
    public String getStd() {
        return std;
    }

    public void setStd(String std) {
        this.std = std;
    }
    public String getdivision() {
        return division;
    }

    public void setdivision(String division) {
        this.division = division;
    }public String getAcademicYr() {
        return academicYr;
    }

    public void setAcademicYr(String academicYr) {
        this.academicYr = academicYr;
    }
    public String getAnnouncementText() {
        return announcementText;
    }

    public void setAnnouncementText(String announcementText) {
        this.announcementText = announcementText;
    }

}