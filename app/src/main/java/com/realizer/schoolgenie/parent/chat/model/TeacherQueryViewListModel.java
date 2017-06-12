package com.realizer.schoolgenie.parent.chat.model;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryViewListModel {

    private String flag="";
    private String senddate = "";
    private String msg="";
    private String subject="";
    private String tname="";
    private String time="";

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    private String profileImage="";

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSenddate() {
        return senddate;
    }

    public void setSenddate(String senddate) {
        this.senddate = senddate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
