package com.realizer.schoolgenie.parent.chat.model;

/**
 * Created by Win on 1/6/2016.
 */
public class TeacherQuerySendModel {

    public int ConversationId=0;
    public String fromTeacher="";
    public String SchoolCode ="";
    public String from ="";
    public String to="";
    public String text ="";
    public String sentTime ="";
    public String hassync="";

    public int getSentDate() {
        return sentDate;
    }

    public void setSentDate(int sentDate) {
        this.sentDate = sentDate;
    }

    public int sentDate = 0;
    public int getConversationId() {
        return ConversationId;
    }

    public void setConversationId(int conversationId) {
        ConversationId = conversationId;
    }

    public String getFromTeacher() {
        return fromTeacher;
    }

    public void setFromTeacher(String fromTeacher) {
        this.fromTeacher = fromTeacher;
    }

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getHassync() {
        return hassync;
    }

    public void setHassync(String hassync) {
        this.hassync = hassync;
    }
}
