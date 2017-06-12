package com.realizer.schoolgenie.parent.timetable.model;

/**
 * Created by Win on 11/20/2015.
 */
public class ParentTimeTableExamListModel {

    public int getTtid() {
        return ttid;
    }

    public void setTtid(int ttid) {
        this.ttid = ttid;
    }

    private int ttid=0;
    private String title = "";
    private String syllabus = "";
    private String teacher = "";
    private String date = "";
    private String image="";
    private String standard = "";
    private String division = "";
    private String schoolcode="";
    private String academicyear = "";
    private String description = "";
    private String filename="";

    public String getAcademicyear() {
        return academicyear;
    }

    public void setAcademicyear(String academicyear) {
        this.academicyear = academicyear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSchoolcode() {
        return schoolcode;
    }

    public void setSchoolcode(String schoolcode) {
        this.schoolcode = schoolcode;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
