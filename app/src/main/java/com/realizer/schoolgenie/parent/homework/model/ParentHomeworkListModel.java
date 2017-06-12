package com.realizer.schoolgenie.parent.homework.model;

/**
 * Created by Win on 11/17/2015.
 */
public class ParentHomeworkListModel {

    private int imgId;
    private String subject="";
    private String homework="";
    private String image="";
    private String hwdate="";
    private String givenBy="";
    private String schoolcode="";
    private String standard="";
    private String division="";
    private String work;
    private String bitmapImg="";
    private String hwUUID="";

    public int getImgId() {return imgId;}

    public void setImgId(int imgId) {this.imgId = imgId;}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHwdate() {
        return hwdate;
    }

    public void setHwdate(String hwdate) {
        this.hwdate = hwdate;
    }

    public String getgivenBy() {
        return givenBy;
    }

    public void setgivenBy(String givenBy) {
        this.givenBy = givenBy;
    }

    public String getschoolcode() {
        return schoolcode;
    }

    public void setschoolcode(String schoolcode) {
        this.schoolcode = schoolcode;
    }

    public String getstandard() {
        return standard;
    }

    public void setstandard(String standard) {
        this.standard = standard;
    }

    public String getdivision() {
        return division;
    }

    public void setdivision(String division) {
        this.division = division;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getBitmapImg() {
        return bitmapImg;
    }

    public void setBitmapImg(String bitmapImg) {
        this.bitmapImg = bitmapImg;
    }

    public String getHwUUID() {
        return hwUUID;
    }

    public void setHwUUID(String hwUUID) {
        this.hwUUID = hwUUID;
    }
}
