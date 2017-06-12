package com.realizer.schoolgenie.parent.chat.model;

import java.util.Date;

/**
 * Created by Win on 1/12/2016.
 */
public class TeacherQuery1model {

    String uname="";
    String uid ="";
    int unreadCount=0;
    String date="";
    String lastMessage="";
    String sendername="";
    Date senddate = null;

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    String profileImg="";

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public Date getSenddate() {
        return senddate;
    }

    public void setSenddate(Date senddate) {
        this.senddate = senddate;
    }
}
